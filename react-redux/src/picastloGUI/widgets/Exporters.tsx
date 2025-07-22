import { useRef, useState } from 'react';
import { PicastLoImage } from '../model/Image';
import { Renderer, TransformationPipeline } from '../model/Transformation'
import { useDispatch, useSelector } from 'react-redux';
import { actionAddPipeline } from '../../store/Pipeline';
import { actionCreateImageFromText } from '../../store/Images';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import { RootState } from '../../store/index';
import { useLocation } from 'react-router-dom';


function save(filename: string, data: BlobPart) {
    const blob = new Blob([data], { type: 'text/csv' });
    const elem = window.document.createElement('a');
    elem.href = window.URL.createObjectURL(blob);
    elem.download = filename;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
}

export const OBJExporter = ({ outputImage }: { outputImage: PicastLoImage | null }) => {

    const saveObj = () => {
        if (!outputImage) return;
        let renderer = new Renderer(outputImage, outputImage, 1);
        let raw: string = renderer.render();
        save('output.obj', raw);
    }

    return <div>
        <button className="button button-dark" onClick={saveObj}>Export to OBJ</button>
    </div>
}

async function send(image: string): Promise<string> {
    return fetch("/piscastlo", { method: "POST", body: btoa(image) })
        .then(req => req.json())
}

export const BlenderExporter = ({ outputImage }: { outputImage: PicastLoImage | null }) => {

    const saveObj = () => {
        if (!outputImage) return;
        let renderer = new Renderer(outputImage, outputImage, 2);
        let raw: string = renderer.render();
        send(raw).then((data: string) => { save('output.stl', atob(data)) });
    }

    return <div>
        <button className="button button-dark" onClick={saveObj}>Export to STL</button>
    </div>
}

export const SVGExporter = () => <div></div>


function saveJSON(data: string, filename: string = 'data.json') {
    const blob = new Blob([data], { type: 'text/application-json' });
    const elem = window.document.createElement('a');
    elem.href = window.URL.createObjectURL(blob);
    elem.download = filename;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
}


export const PipelineExporter = ({ pipeline }: { pipeline: TransformationPipeline }) => {
    
    const saveObj = () => {
        save('pipeline.json', pipeline.toJSON());
    }

    return <button className="button button-dark" onClick={saveObj}>Save</button>


}

export const PipelineExporter2Social = ({ pipeline }: { pipeline: TransformationPipeline }) => {
    const dispatch: any = useDispatch();
    const location = useLocation();

    const isLoggedIn = useSelector((state: RootState) => state.auth.isLoggedIn);

    const [open, setOpen] = useState(false);
    const [pipelineName, setPipelineName] = useState("");
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");

    const handleSave = async () => {
        if (pipelineName && description) {
            try {
                
                var img2picastlo = localStorage.getItem("image2Picastlo");
                if(location.pathname === '/picastlo/loadedPipeline' && img2picastlo){
                    await dispatch(actionAddPipeline(pipelineName, description, parseInt(img2picastlo, 10), pipeline.toJSON()));
                }
                else{
                    await dispatch(actionCreateImageFromText(localStorage.getItem("newPipelineImage") || ""));
                    await dispatch(actionAddPipeline(pipelineName, description, parseInt(localStorage.getItem("newPipelineImage") || "0", 10), pipeline.toJSON()));
                }
                
                handleClose();
            } catch (e) {
                setError("An error occurred while saving the pipeline. Please try again.");
            }
        } else {
            alert("Please fill in all fields.");
        }
    };

    const handleClose = () => {
        setOpen(false);
        setPipelineName("");
        setDescription("");
    };

    return (
        <>
            <button className="button button-dark" onClick={() => setOpen(true)} hidden={!isLoggedIn}>Save to Picastlo Social</button>
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Save Pipeline</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Pipeline Name"
                        fullWidth
                        value={pipelineName}
                        onChange={(e) => setPipelineName(e.target.value)}
                    />
                    <TextField
                        margin="dense"
                        label="Description"
                        fullWidth
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                    {error && <p style={{ color: "red" }}>{error}</p>}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={handleSave} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

interface PipelineLoaderInterface {
    pipeline: TransformationPipeline,
    onLoad: (selected:number) => void
}

export const PipelineLoader = ({pipeline, onLoad}:PipelineLoaderInterface) => {

    const [error, setError] = useState<string | null>(null)

    const inputRef = useRef<HTMLInputElement>(null)
    const clickButton = () => {
        inputRef.current?.click()
    }

    const loadFile = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e: ProgressEvent<FileReader>) => {
                try{
                    pipeline.fromJSON(e.target?.result as string)
                    onLoad(pipeline.length())
                } catch(error:any) {
                    setError(error.message)
                }
            };
            reader.readAsText(file);
        }
    };


    return <>
            <button className="button button-dark" onClick={clickButton}>Load</button>
            <input ref={inputRef} style={{display:"none"}} accept=".json" type="file" onChange={loadFile}/>
            {error && <p style={{color:'red'}}>{error}<span style={{ cursor: 'grab', height:'0px', position:'relative',top:'0px',left:'300px'}} onClick={()=>setError(null)}>x</span></p>}
           </>
}