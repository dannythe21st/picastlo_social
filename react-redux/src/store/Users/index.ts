import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UsersApi, Configuration, LoginUserDTO, UserDTO} from '../../api';
import { logIn, logOut } from '../Auth/index';

export interface UsersState {
    users: UserDTO[];
    loading: boolean;
    uploading: boolean;
    totalCount: number;
}

const initialState: UsersState = {
    users: [],
    loading: false,
    uploading: false,
    totalCount: 0
};

const slice = createSlice({
    name: 'users',
    initialState,
    reducers: {
        createUser: (state, action: PayloadAction<UserDTO>) => {
            state.users = [...state.users, action.payload];
        },
        setUsers: (state, action: PayloadAction<UserDTO[]>) => {
            state.users = action.payload;
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

export const { createUser, setLoading, setUploading, setUsers, setTotalCount } = slice.actions;


var api: UsersApi;

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new UsersApi(config);
};


export const actionLogin = (username: string, password: string) => async (dispatch: any) => {
    try {
      dispatch(setLoading(true));
      initializeApi()
      const loginUser: LoginUserDTO = { username, password };
      const response = await api.loginUser({ loginUserDTO: loginUser });

      if (response) {
        
            var token = localStorage.getItem("authToken");
            console.log("TOKEN LOGIN >>> "+token)
            if(token!=null){
                dispatch(logIn());
                localStorage.setItem("username",username)
                initializeApi()
            }
            else{
                alert("token não encontrada")
                dispatch(logOut());
            }
            
        }
        else{
            dispatch(logOut());
            throw new Error("UNAUTHORIZED");
        }
      
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      dispatch(logOut());
      throw new Error("UNAUTHORIZED");
    } finally {
      dispatch(setLoading(false));
    }
  };


export const actionLoadUsers = (page: number = 0, size: number = 10) => async (dispatch: any,getState: any) => {
    const { users, totalCount } = getState().users;
    
    const isPageLoaded = users.slice(page * size, (page + 1) * size).length === size;
    
    if (totalCount === 0 || !isPageLoaded) {
        dispatch(setLoading(true));
        initializeApi()
    }
    try {
        //dispatch(setLoading(true));
        const usersResponse = await api.getUsers({ page, size });

        const currentUsers = getState().users.users;

        const allUsersExist = usersResponse.list.every(newUser =>
            currentUsers.some((existingUser: any) => JSON.stringify(existingUser) === JSON.stringify(newUser))
        );
        dispatch(setTotalCount(usersResponse.max));
        if (!allUsersExist) {
            dispatch(setUsers([...currentUsers, ...usersResponse.list]));
        }
    } catch (error) {
        console.error("Erro ao carregar users:", error);
        dispatch(setLoading(false));
    }
    finally {
        dispatch(setLoading(false));
    }
};

export const actionRegisterUser = (username: string,password: string) => async (dispatch: any) => {
    try {
        dispatch(setUploading(true));
        const newUser = { username: username, password : password};
        await api.createUser({ createUserDTO: newUser });
        dispatch(createUser(newUser));
    } catch (error) {
        console.error("Erro ao adicionar user:", error);
    } finally {
        dispatch(setUploading(false));
    }
};

export const actionGetUserFriendsList = () => async (dispatch: any) => {
    /*try {
        dispatch(setUploading(true));
        const newUser = { username: username, password : password};
        await api.createUser({ createUserDTO: newUser });
        dispatch(createUser(username));
    } catch (error) {
        console.error("Erro ao adicionar user:", error);
    } finally {
        dispatch(setUploading(false));
    }*/
};

export default slice.reducer; 


