import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { PostsApi, Configuration } from '../../api';
import {PostDTOVisibilityEnum} from '../../api';
import { PostDTO } from '../../api';

export interface PostGroupFeedState {
    posts: PostDTO[];
    groupId: number,
    loading: boolean;
    uploading: boolean;
    totalCount: number;
}

const initialState: PostGroupFeedState = {
    posts: [],
    groupId: 0,
    loading: false,
    uploading: false,
    totalCount: 0
};

const postGroupFeedSlice = createSlice({
    name: 'groupFeed',
    initialState,
    reducers: {
        createPost: (state, action: PayloadAction<PostDTO>) => {
            const exists = state.posts.some(post => post.id === action.payload.id);
            if (!exists) {
                state.posts.push(action.payload);
            }
        },
        setPosts: (state, action: PayloadAction<PostDTO[]>) => {
            state.posts = action.payload;
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
        setGroupId: (state, action: PayloadAction<number>) => {
            state.groupId = action.payload;
        },
    },
});

export const { setPosts, setLoading, setUploading, createPost,setTotalCount,setGroupId } = postGroupFeedSlice.actions;


var api: PostsApi;

const initializeApi = () => {
    const config = new Configuration({
        basePath: 'http://localhost:3000',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('authToken') || ''}`,
        },
    });

    api = new PostsApi(config);
};


export const actionLoadGroupFeedPosts = (groupId: number, page: number = 0, size: number = 10) => async (dispatch: any,getState: any) => {
    const { posts, totalCount } = getState().groupFeed;

    const isPageLoaded = posts.slice(page * size, (page + 1) * size).length >= size;
    if (totalCount === 0 || !isPageLoaded) {
        dispatch(setLoading(true));
        initializeApi()
    }
    try {
        const postsResponse = await api.getGroupFeed({groupId, page, size });
        
        const currentPosts = getState().groupFeed.posts;
  
          const allPostsExist = postsResponse.list.every(newPost =>
              currentPosts.some((existingPost: any) => JSON.stringify(existingPost) === JSON.stringify(newPost))
          );

          dispatch(setTotalCount(postsResponse.max));
          dispatch(setGroupId(groupId));

          if (!allPostsExist) {
              dispatch(setPosts([...currentPosts, ...postsResponse.list]));
          }

        } catch (error) {
            console.error("Error loading feed posts:", error);
        }
        finally {
            dispatch(setLoading(false));
        }
};



export const actionAddPost = ( user: string, image: number, pipeline: number, 
    groupId: number, text: string, visibility: PostDTOVisibilityEnum, createdAt: Date
 ) => async (dispatch: any, getState: any) => {
     const { posts } = getState().postsFeed;

     if(posts.length === 0){
         dispatch(setUploading(true));
         initializeApi()
     }
    try {
         var id:number = 0;
        const newPost = await api.publishPost({ postDTO: { id , user, image, pipeline, text, visibility, createdAt: new Date(createdAt) }});
        dispatch(createPost(newPost));
    } catch (error) {
        console.error("Error adding post:", error);
    } finally {
        dispatch(setUploading(false));
    }
};


export default postGroupFeedSlice.reducer;
