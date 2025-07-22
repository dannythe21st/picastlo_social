import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Configuration, FriendsApi } from '../../api';

export interface FriendsState {
    friends: string[];
    loading: boolean;
    uploading: boolean;
}

const initialState: FriendsState = {
    friends: [],
    loading: false,
    uploading: false,
};

const friendsSlice = createSlice({
    name: 'friends',
    initialState,
    reducers: {
        addFriend: (state, action: PayloadAction<string>) => {
            state.friends.push(action.payload);
        },
        removeFriend: (state, action: PayloadAction<string>) => {
            state.friends = state.friends.filter((friend) => friend !== action.payload);
        },
        setFriends: (state, action: PayloadAction<string[]>) => {
            state.friends = action.payload;
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

export const { addFriend, removeFriend, setFriends, setLoading, setUploading } = friendsSlice.actions;


var api : FriendsApi

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new FriendsApi(config);
};


export const actionLoadFriends = (username: string, filter: string = '') => async (dispatch: any, getState: any) => {
    if(getState().friends.friends.length===0){
        dispatch(setLoading(true));
        initializeApi();
    }  
    try {
        const friends = await api.getFriendsList({ username });
        dispatch(setFriends(friends));
    } catch (error) {
        console.error('Erro ao carregar amigos:', error);
        
    }
    finally {
        dispatch(setLoading(false));
    }
};


export const actionAddFriend = (username: string) => async (dispatch: any, getState: any) => {
    if(getState().friends.friends.length===0){
        dispatch(setUploading(true));
        initializeApi();
    }  
    try {
        await api.addFriend({ username });
        dispatch(addFriend(username));
    } catch (error) {
        console.error('Erro ao adicionar amigo:', error);
    } finally {
        dispatch(setUploading(false));
    }
};


export const actionRemoveFriend = (username: string) => async (dispatch: any) => {
    try {
        //dispatch(setUploading(true));
        await api.removeFriend({username});
        dispatch(removeFriend(username));
    } catch (error) {
        console.error('Erro ao remover amigo:', error);
    } finally {
        //dispatch(setUploading(false));
    }
};

export default friendsSlice.reducer;


