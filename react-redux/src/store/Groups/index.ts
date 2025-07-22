import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Configuration, GroupDTO, GroupsApi} from '../../api';



export interface GroupsState {
    groups: GroupDTO[];
    loading: boolean;
    uploading: boolean;
    totalCount: number;
}

const initialState: GroupsState = {
    groups: [],
    loading: false,
    uploading: false,
    totalCount: 0
};

const slice = createSlice({
    name: 'groups',
    initialState,
    reducers: {
        createGroup: (state, action: PayloadAction<GroupDTO>) => {
            state.groups = [...state.groups, action.payload];
        },
        setGroups: (state, action: PayloadAction<GroupDTO[]>) => {
            state.groups = action.payload;
            state.loading = false;
        },
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setUploading: (state, action: PayloadAction<boolean>) => {
            state.uploading = action.payload;
        },
        setTotalCount: (state, action: PayloadAction<number>) => {
            state.totalCount = action.payload;
        },
    },
});

export const { createGroup, setLoading, setUploading, setGroups, setTotalCount } = slice.actions;


var api : GroupsApi;

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new GroupsApi(config);
};



export const actionLoadGroups = (page: number = 0, size: number = 10) => async (dispatch: any,getState: any) => {
    const { groups, totalCount } = getState().groups;

    const isPageLoaded = groups.slice(page * size, (page + 1) * size).length === size;
    
    if (totalCount === 0 || !isPageLoaded) {
        dispatch(setLoading(true));
        initializeApi()
    }
    try {
        dispatch(setLoading(true));
        const groupsResponse = await api.getMyGroups({page,size});

        const currentGroups = getState().groups.groups;

        const allGroupsExist = groupsResponse.list.every(newGroup =>
            currentGroups.some((existingGroup: any) => JSON.stringify(existingGroup) === JSON.stringify(newGroup))
        );
        dispatch(setTotalCount(groupsResponse.max));
        if (!allGroupsExist) {
            dispatch(setGroups([...currentGroups, ...groupsResponse.list]));
        }
    } catch (error) {
        console.error("Erro ao carregar groups:", error);
        dispatch(setLoading(false));
    }
    finally {
        dispatch(setLoading(false));
    }
};

export const actionCreateGroup = (id: number, name: string, owner: string, members: Set<string>) => async (dispatch: any) => {
    try {
        dispatch(setUploading(true));
        const newGroup = { id: id, name : name, owner: owner, members: members};
        await api.createGroup({ createGroupDTO: newGroup });
        dispatch(createGroup(newGroup));
    } catch (error) {
        console.error("Erro ao adicionar user:", error);
    } finally {
        dispatch(setUploading(false));
    }
};


export default slice.reducer; 


