import { configureStore } from "@reduxjs/toolkit"
import { logger } from 'redux-logger'
import PostsReducer,{ PostState } from "./Post"
import UsersReducer, { UsersState } from "./Users"
import PipelinesReducer, { PipelinesState } from "./Pipeline"
import ImageReducer, { ImagesState } from "./Images"
import AuthReducer, { AuthState } from "./Auth"
import FriendsReducer, { FriendsState } from "./Friends"
import PostsFeedReducer, { PostFeedState } from "./Post Feed"
import GroupsReducer, { GroupsState } from "./Groups"
import PostGroupFeedReducer, { PostGroupFeedState } from "./Post Group Feed"

export interface GlobalState {
    images: ImagesState,
    posts: PostState,
    users: UsersState,
    pipelines: PipelinesState,
    auth: AuthState,
    friends: FriendsState,
    postsFeed: PostFeedState,
    groups: GroupsState,
    groupFeed: PostGroupFeedState
}

export const store = configureStore({
    reducer : {
        images: ImageReducer,
        posts: PostsReducer,
        postsFeed: PostsFeedReducer,
        pipelines: PipelinesReducer,
        users: UsersReducer,
        auth: AuthReducer,
        friends: FriendsReducer,
        groups: GroupsReducer,
        groupFeed: PostGroupFeedReducer
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat([logger]),
})

export type RootState = ReturnType<typeof store.getState>;

//store.dispatch(actionLoadBooks())