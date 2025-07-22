import '../App.css';
import React, { MouseEventHandler, ReactNode, useEffect, useRef, useState } from 'react';
import { TransformationPipeline, Transformation } from './model/Transformation' 
import { ToolBox } from './Toolbox'
import { useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { actionGetImage } from '../store/Images';
import { PicastLoImage } from './model/Image'; // Import the correct class
import { ImageDTO } from '../api';

const initialPipeline: TransformationPipeline = new TransformationPipeline([])

const App: React.FC = () => {
  const location = useLocation();
  const dispatch: any = useDispatch();
  const images: ImageDTO[] = useSelector((state: any) => state.images.images);
  
  const pipeline = initialPipeline
  const [tick, setTick] = useState(0)
  const [image, setImage] = useState<string | null>(null);
  const [selected, setSelected] = useState(pipeline.length()-1)
  const [loadedFromSocial, setLoadedFromSocial] = useState(0);
  

  const addTransform = (index:number, transform:Transformation) => {
    pipeline.insertTransformation(index, transform)
    setTick(() => tick+1) // Just because we are using objects in the pipeline that do not change
  }

  const removeTransform = (index:number) => {
    pipeline.removeTransformation(index)
    if(pipeline.length() === 0) setSelected(-1)
    else if(pipeline.length() === index ) setSelected(pipeline.length()-1)
    setTick(() => tick+1) // Just because we are using objects in the pipeline that do not change
  }


  const imgRef = useRef(null as unknown as HTMLImageElement)
  const cellRef = useRef(null as unknown as HTMLDivElement)
  const toolbarRef = useRef(null as unknown as HTMLDivElement)
  const licRef = useRef(null as unknown as HTMLDivElement)

  const imageCanvas = image && <> 
      <img ref={imgRef} src={image} alt="Uploaded" className="uploaded-image" />
    </>


  const transform_step = (i: number) => {
    setSelected(i)
}

const addPipelineAndPerformeTransformations = (loadedPipeline: string,picastloImage: PicastLoImage) => {
  try {
    const parsedPipeline = JSON.parse(loadedPipeline);
    pipeline.fromJSON(parsedPipeline);
    pipeline.setInitialImage(picastloImage);
    pipeline.performTransformations(0,transform_step)
    setTick((prev) => prev + 1);
    setSelected(pipeline.getPipeline().length-1)
    setLoadedFromSocial(loadedFromSocial+1)
} catch (error) {
    console.error("Failed to parse pipeline from localStorage", error);
}
}


  useEffect(() => {
    cellRef.current.style.width = `calc(100vw - ${toolbarRef.current.clientWidth}px)`
    licRef.current.style.width = `${toolbarRef.current.clientWidth-20}px`
    
        if (location.pathname === '/picastlo/loadedPipeline') {
    
          const loadedPipeline = localStorage.getItem("pipeline2Picastlo");
          const loadedImageId = localStorage.getItem("image2Picastlo");
          if (loadedImageId) {
            const foundImage = images.find((img: any) => img.id === Number(loadedImageId));
            if (foundImage) {
              const imageSrc = `data:image/png;base64,${foundImage.image}`;
              localStorage.setItem("newPipelineImage",foundImage.image)
              setImage(imageSrc);
              const picastloImage = new PicastLoImage(null);
              picastloImage.loadFromString(imageSrc).then(() => {
                  if (loadedPipeline) {
                    addPipelineAndPerformeTransformations(loadedPipeline,picastloImage)
                  }
              });
          } else {
            dispatch(actionGetImage(Number(loadedImageId)))
                .then((response: any) => {
                    const newImage = response.payload;
                    const imageSrc = `data:image/png;base64,${newImage.image}`;
                    localStorage.setItem("newPipelineImage",newImage.image)
                    setImage(imageSrc);
                    const picastloImage = new PicastLoImage(null);
                    picastloImage.loadFromString(imageSrc).then(() => {
                        if (loadedPipeline) {
                          addPipelineAndPerformeTransformations(loadedPipeline,picastloImage)
                        }
                    });
                })
                .catch((error: any) => {
                    console.error("Failed to load image from API >> ", error);
                });
        } 
        
        }
        
      }/*else{
        localStorage.removeItem("image2Picastlo");
        localStorage.removeItem("pipeline2Picastlo");
      }*/
  }, [location.pathname, images])

  

  useEffect(() => {
    const img = imgRef.current
    if(!img)
      return
    
    img.style.left = `${(cellRef.current.clientWidth - img.clientWidth)/2}px`
  }, [tick,selected,image])
  
  return (<>
    <div className="image-transformer" style={{ marginTop: "9vh" }}>
      <div className="image-container">

        <div ref={cellRef} className="image-cell">{imageCanvas}</div>

        <div ref={toolbarRef} className="toolbox">
          <h1 >PicaSTLo</h1>
          <div className='transformations'>
              <ToolBox imgRef={imgRef} pipeline={pipeline} image={image} setImage={setImage} addTransform={addTransform} removeTransform={removeTransform} selected={selected} setSelected={setSelected} loadedFromSocial={loadedFromSocial}/>
              <div ref={licRef} className='license'>
                <a href='https://picastlo.github.io/'>Picastlo</a> © 2024 by <a href='https://github.com/picastlo/'>Christophe Scholliers, João Costa Seco, Eduardo Geraldo</a> is licensed under <a href='https://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1'>CC BY-SA 4.0</a>
              </div>
          </div>
        </div>
      </div>
    </div>
    </>
  );  
};

export default App;


