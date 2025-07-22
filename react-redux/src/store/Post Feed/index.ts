import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { PostsApi, Configuration } from '../../api';
import {PostDTOVisibilityEnum} from '../../api';
import { PostDTO } from '../../api';
import { useSelector } from 'react-redux';

export interface PostFeedState {
    posts: PostDTO[];
    loading: boolean;
    uploading: boolean;
    totalCount: number;
}

const initialState: PostFeedState = {
    posts: [],
    loading: false,
    uploading: false,
    totalCount: 0
};

const postFeedSlice = createSlice({
    name: 'postsFeed',
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
    },
});

export const { setPosts, setLoading, setUploading, createPost,setTotalCount } = postFeedSlice.actions;




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


export const actionLoadFeedPosts = (page: number = 0, size: number = 10) => async (dispatch: any,getState: any) => {
    const { posts, totalCount } = getState().postsFeed;

    const isPageLoaded = posts.slice(page * size, (page + 1) * size).length >= size;
    if (totalCount === 0 || !isPageLoaded) {
        dispatch(setLoading(true));
        initializeApi();
    }

    try {
        const PostResponse = await api.getUserFeed({ page, size });

          const allPostsExist = PostResponse.list.every(newPost =>
            posts.some((existingPost: any) => JSON.stringify(existingPost) === JSON.stringify(newPost))
          );
          dispatch(setTotalCount(PostResponse.max));

          if (!allPostsExist) {
              dispatch(setPosts([...posts, ...PostResponse.list]));
          }

           } catch (error) {
            console.error("Error loading feed posts:", error);
            dispatch(setLoading(false));
        }
        finally {
            dispatch(setLoading(false));
        }
};

export const actionLoadPublicFeedPosts = (page: number = 0, size: number = 10) => 
    async (dispatch: any, getState: any) => {
    const { posts, totalCount } = getState().postsFeed;

    const isPageLoaded = posts.slice(page * size, (page + 1) * size).length >= size;

    if(totalCount===0 || !isPageLoaded){
            dispatch(setLoading(true));
            initializeApi();
    }  
      try {
          const postsResponse = await api.getPublicPosts({ page, size });
          console.log("data 2: " + JSON.stringify(posts));
          
  
          const allPostsExist = postsResponse.list.every(newPost =>
            posts.some((existingPost: any) => JSON.stringify(existingPost) === JSON.stringify(newPost))
          );
          dispatch(setTotalCount(postsResponse.max));
          if (!allPostsExist) {
              dispatch(setPosts([...posts, ...postsResponse.list]));
          }
  
      } catch (error) {
          console.error("Error loading public posts:", error);
      } finally {
          dispatch(setLoading(false));
      }
  };
  


export default postFeedSlice.reducer;
