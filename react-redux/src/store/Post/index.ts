import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { PostsApi, Configuration } from "../../api";
import { PostDTOVisibilityEnum } from "../../api";
import { PostDTO } from "../../api";

export interface PostState {
  posts: PostDTO[];
  loading: boolean;
  uploading: boolean;
  totalCount: number;
}

const initialState: PostState = {
  posts: [],
  loading: false,
  uploading: false,
  totalCount: 0,
};

const slice = createSlice({
  name: "posts",
  initialState,
  reducers: {
    createPost: (state, action: PayloadAction<PostDTO>) => {
      const exists = state.posts.some((post) => post.id === action.payload.id);
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
    clearPosts: (state) => {
      state.posts = [];
      state.totalCount = 0;
    },
  },
});

export const {
  setPosts,
  setLoading,
  setUploading,
  createPost,
  setTotalCount,
  clearPosts,
} = slice.actions;

var api: PostsApi;

const initializeApi = () => {
  const config = new Configuration({
    basePath: "http://localhost:3000",
    headers: {
      Authorization: `Bearer ${localStorage.getItem("authToken") || ""}`,
    },
  });

  api = new PostsApi(config);
};

export const actionLoadMyPosts =
  (page: number = 0, size: number = 6) =>
  async (dispatch: any, getState: any) => {
    const { posts, totalCount } = getState().posts;
    const isPageLoaded =
      posts.slice(page * size, (page + 1) * size).length >= size;
    if (totalCount === 0) {
      console.log(
        "EI - totalCount= " + totalCount + " e isPageLoaded=" + isPageLoaded
      );
      console.log(">>>> page = " + page);
      console.log(">>>> size = " + size);
      dispatch(setLoading(true));
      initializeApi();
    }
    try {
      const postsResponse = await api.getMyPosts({ page, size });

      const allPostsExist = postsResponse.list.every((newPost) =>
        posts.some(
          (existingPost: any) =>
            JSON.stringify(existingPost) === JSON.stringify(newPost)
        )
      );
      dispatch(setTotalCount(postsResponse.max));

      if (!allPostsExist) {
        dispatch(setPosts([...posts, ...postsResponse.list]));
      }
    } catch (error) {
      console.error("Error loading my posts:", error);
    } finally {
      dispatch(setLoading(false));
    }
  };

export const actionLoadUserPosts =
  (username: string, page: number = 0, size: number = 6) =>
  async (dispatch: any, getState: any) => {
    const { posts, totalCount } = getState().posts;
    const isPageLoaded =
      posts.slice(page * size, (page + 1) * size).length >= size;
    if (totalCount === 0) {
      console.log(
        "EI - totalCount= " + totalCount + " e isPageLoaded=" + isPageLoaded
      );
      console.log(">>>> page = " + page);
      console.log(">>>> size = " + size);
      dispatch(setLoading(true));
      initializeApi();
    }
    try {
      const postsResponse = await api.getUserPosts({ username, page, size });

      const allPostsExist = postsResponse.list.every((newPost) =>
        posts.some(
          (existingPost: any) =>
            JSON.stringify(existingPost) === JSON.stringify(newPost)
        )
      );
      dispatch(setTotalCount(postsResponse.max));

      if (!allPostsExist) {
        dispatch(setPosts([...posts, ...postsResponse.list]));
      }
    } catch (error) {
      console.error("Error loading user posts:", error);
    } finally {
      dispatch(setLoading(false));
    }
  };

export const actionClearPosts = () => async (dispatch: any, getState: any) => {
  dispatch(clearPosts());
};

export const actionAddPost =
  (
    user: string,
    image: number,
    pipeline: number,
    groupId: number,
    text: string,
    visibility: PostDTOVisibilityEnum,
    createdAt: Date
  ) =>
  async (dispatch: any, getState: any) => {
    const { posts } = getState().postsFeed;
    if (posts.length === 0) {
      dispatch(setUploading(true));
      initializeApi();
    }
    try {
      var id: number = 0;
      const newPost = await api.publishPost({
        postDTO: {
          id,
          user,
          image,
          pipeline,
          text,
          visibility,
          createdAt: new Date(createdAt),
        },
      });
      dispatch(createPost(newPost));
    } catch (error) {
      console.error("Error adding post:", error);
    } finally {
      dispatch(setUploading(false));
    }
  };

export default slice.reducer;
