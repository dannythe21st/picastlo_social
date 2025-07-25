import { Foldable } from "./Utils";
import { PipelineExporter,PipelineExporter2Social, OBJExporter, PipelineLoader } from "./Exporters";
import { PicastLoImage } from "../model/Image";
import { TransformationPipeline } from "../model/Transformation";

interface ExportersInterface { 
    onSelect: () => void, 
    image:PicastLoImage|null,
    pipeline:TransformationPipeline,
    onLoad: (selected:number) => void
}

const ImageTools = ({ onSelect, image, pipeline, onLoad }:ExportersInterface) => {

    return (
        <div className="widget-card" onClick={onSelect}>
            <Foldable title={`Final Image`}>
                <div><OBJExporter outputImage={image} /></div>
                <div>
                    <PipelineExporter pipeline={pipeline} />
                    <PipelineLoader pipeline={pipeline} onLoad={onLoad}/>
                </div>
                <div><PipelineExporter2Social pipeline={pipeline} /></div>
            </Foldable>
        </div>
    )
}

export default ImageTools