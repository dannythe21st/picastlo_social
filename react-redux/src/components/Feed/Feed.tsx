import React, { useEffect, useState, useCallback } from 'react';
import PostCard from '../Post/PostCard';
import { CircularProgress, Pagination, Box } from '@mui/material';
import { actionLoadFeedPosts, actionLoadPublicFeedPosts } from '../../store/Post Feed';
import { useDispatch, useSelector } from 'react-redux';
import { PostDTO } from '../../api';

const Feed: React.FC = () => {
  const postsCache = useSelector((state: any) => state.postsFeed.posts);
  const loading = useSelector((state: any) => state.postsFeed.loading);
  const totalPosts = useSelector((state: any) => state.postsFeed.totalCount);

  const [page, setPage] = useState(1);
  const dispatch: any = useDispatch();
  
  const postsPerPage = 5;
  const startIndex = (page - 1) * postsPerPage;
  const endIndex = startIndex + postsPerPage;

  const currentPosts = postsCache.slice(startIndex, endIndex);
  const fetchPosts = useCallback(() => {
    
    if(localStorage.getItem("username")!=null){
      console.log("GET USER FEED")
      dispatch(actionLoadFeedPosts(page - 1, postsPerPage));
    }
    else{
        console.log("GET PUBLIC FEED")
        dispatch(actionLoadPublicFeedPosts(page - 1, postsPerPage));
    }
      
      
  }, [page, dispatch]);

  useEffect(() => {
    fetchPosts();
  }, [fetchPosts]);

  const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  return (
    <div
      style={{
        paddingTop: '64px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        minHeight: '100vh',
      }}
    >
      <div style={{ width: '100%', maxWidth: '600px' }}>
        {loading && currentPosts.length === 0 ? (
          <div style={{ display: 'flex', justifyContent: 'center', margin: '20px' }}>
            <CircularProgress />
          </div>
        ) : (
          currentPosts.map((post: PostDTO) => (
            <div key={post.id} style={{ display: 'flex', justifyContent: 'center', margin: '20px' }}>
              <PostCard post={post} avatarVis={true} />
            </div>
          ))
        )}
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          marginTop="20px"
          marginBottom="20px"
        >
          <Pagination
            count={Math.ceil(totalPosts / postsPerPage)}
            page={page}
            onChange={handleChange}
            color="primary"
            variant="outlined"
            shape="rounded"
          />
        </Box>
      </div>
    </div>
  );
};

export default Feed;
