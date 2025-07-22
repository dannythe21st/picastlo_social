import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Configuration, ImageDTO, ImagesApi } from '../../api';
import { VaccinesRounded } from '@mui/icons-material';

export interface ImagesState {
    images: ImageDTO[];
    loading: boolean;
    uploading: boolean;
}

const initialState: ImagesState = { images: [], loading: false, uploading: false };

const slice = createSlice({
    name: 'images',
    initialState,
    reducers: {
        addImage: (state, action: PayloadAction<ImageDTO>) => {
            const exists = state.images.some(image => image.id === action.payload.id);
            if (!exists) {
                state.images = [...state.images, action.payload];
            }
        },
        setImages: (state, action: PayloadAction<ImageDTO[]>) => {
            state.images = action.payload;
            state.loading = false;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setUploading: (state, action: PayloadAction<boolean>) => {
            state.uploading = action.payload;
        },
    },
});

export const { addImage, setImages, setLoading, setUploading } = slice.actions;


var api: ImagesApi

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new ImagesApi(config);
};

export const actionGetImage = (id: number) => async (dispatch: any,getState: any) => {
    const { images } = getState().images; 
    const isImageLoaded = images.some((image: any) => image.id === id);

    if (images.length === 0) {
        dispatch(setLoading(true));
        initializeApi()
    }
    if(!isImageLoaded){
        try {
            const images = await api.getImage({ id });
            dispatch(addImage(images));
        } catch (error) {
            console.error('1 - Erro ao obter imagens:', error);
        } finally {
            dispatch(setLoading(false));
        }
    }
    
};

export const actionGetPublicImage = (id: number) => async (dispatch: any,getState: any) => {
    const { images } = getState().images; 
    const isImageLoaded = images.some((image: any) => image.id === id);

    if (images.length === 0) {
        dispatch(setLoading(true));
        initializeApi()
    }
    if(!isImageLoaded){
        try {
            const imageResponse = await api.getPublicImage({ id });
            dispatch(addImage(imageResponse));
        } catch (error) {
            console.error('2 - Erro ao obter imagens:', error);
        } finally {
            dispatch(setLoading(false));
        }
    }
    
};

export const actionAddImage = (file: Blob) => async (dispatch: any) => {
    dispatch(setUploading(true));

    try {
        const image = await api.createImage({ file });
        dispatch(addImage(image));
    } catch (error) {
        console.error('Erro ao adicionar imagem:', error);
    } finally {
        dispatch(setUploading(false));
    }
};

export const actionCreateImageFromText = (imageBase64: string) => async (dispatch: any) => {
    dispatch(setUploading(true));

    try {
        const image = await api.createImageFromText({ imageBase64 });
        localStorage.setItem("newPipelineImage",image.image)
        dispatch(addImage(image));
    } catch (error) {
        console.error('Erro ao adicionar imagem:', error);
    } finally {
        dispatch(setUploading(false));
    }
};


export default slice.reducer;
