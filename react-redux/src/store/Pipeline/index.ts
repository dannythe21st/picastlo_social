import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { PipelinesApi, Configuration, PipelineDTO } from '../../api';

export interface PipelinesState {
    pipelines: PipelineDTO[];
    loading: boolean;
    uploading: boolean;
}

const initialState: PipelinesState = { pipelines: [], loading: false, uploading: false };

const slice = createSlice({
    name: 'pipelines',
    initialState,
    reducers: {
        addPipeline: (state, action: PayloadAction<PipelineDTO>) => {
            state.pipelines.push(action.payload);
        },
        setPipelines: (state, action: PayloadAction<PipelineDTO[]>) => {
            state.pipelines = action.payload;
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

const { addPipeline, setPipelines, setLoading, setUploading } = slice.actions;


var api : PipelinesApi;

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new PipelinesApi(config);
};

export const actionGetPipelines = (username: string) => async (dispatch: any,getState: any) => {
    const { pipelines } = getState().pipelines; 
    if(pipelines.length === 0){
        dispatch(setLoading(true));
        initializeApi();
    }
    try {
        const pipelines = await api.getUserPipelines({username});
        dispatch(setPipelines(pipelines));
    } catch (error) {
        console.error("Failed to fetch pipelines", error);
    } finally {
        dispatch(setLoading(false));
    }
};

export const actionAddPipeline = (name: string, description: string, id: number, transformations: string) => async (dispatch: any,getState: any) => {
    const { pipelines } = getState().pipelines; 
    if(pipelines.length === 0){
        dispatch(setUploading(true));
        initializeApi();
    }
    try {
        console.log(description)
        console.log(name)
        console.log(id)
        console.log(transformations)
        const newPipeline = await api.createPipeline({description, name, id, transformations});
        dispatch(addPipeline(newPipeline));
    } catch (error) {
        console.error("Failed to add pipeline", error);
    } finally {
        dispatch(setUploading(false));
    }
};

export default slice.reducer;
