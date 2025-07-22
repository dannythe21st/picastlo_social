import { Box, CardMedia, CircularProgress } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import { actionGetImage, actionGetPublicImage } from "../../store/Images";
import React, { useEffect,useState,useCallback } from "react";

type Base64ImageProps = {
    imageId: number,
  alt?: string;
};
    

const Base64Image: React.FC<Base64ImageProps> = ({imageId, alt}) => {
    
    const images = useSelector((state: any) => state.images.images);
    const imageLoading = useSelector((state: any) => state.images.loading);
    const dispatch: any = useDispatch();
    const [formattedBase64, setFormattedBase64] = useState('');
 
    const fetchImages = useCallback(() => {
          if(localStorage.getItem("username")!=null){
            console.log("GET IMAGE "+imageId)
    
            dispatch(actionGetImage(imageId));
          }
          else{
              console.log("GET PUBLIC IMAGE "+imageId)
              dispatch(actionGetPublicImage(imageId));
          }
    }, [imageId]);

    useEffect(() => {
        if (images.length > 0) {
          const foundImage = images.find((img: any) => img.id === imageId);
          if (foundImage) {
            setFormattedBase64(`data:image/png;base64,${foundImage.image}`);
          }
          else{
            console.log("get Image "+imageId)
            fetchImages()
          }
        }
        else{
          fetchImages()
        }
      }, [images, imageId,fetchImages]);


    return(

        <Box sx={{ mt: 2 }}>
        {imageLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center" }}>
          <CircularProgress />
        </Box>
        ) : (
        <CardMedia
          component="img"
          height="194"
          image={formattedBase64}
          alt={alt}
        />
      )}
    </Box>
    );

};

export default Base64Image;
