import React, { useEffect, useState, useCallback } from 'react';
import PostCard from '../Post/PostCard';
import { CircularProgress, Pagination, Box } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { PostDTO } from '../../api';
import { useParams } from "react-router-dom";
import { actionLoadGroupFeedPosts } from '../../store/Post Group Feed';


const GroupFeed: React.FC = () => {
  const { id } = useParams<{ id: string}>();
  const groupId = parseInt(id || "0", 10);

  const postsCache = useSelector((state: any) => state.groupFeed.posts);
  const loading = useSelector((state: any) => state.groupFeed.loading);
  const totalPosts = useSelector((state: any) => state.groupFeed.totalCount);
  const [page, setPage] = useState(1);
  const dispatch: any = useDispatch();
  const postsPerPage = 10;
  const startIndex = (page - 1) * postsPerPage;
  const endIndex = startIndex + postsPerPage;

  const currentPosts = postsCache.slice(startIndex, endIndex);
  

  useEffect(() => {
    dispatch(actionLoadGroupFeedPosts(groupId,page - 1, postsPerPage));
  }, [dispatch,page]);

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

export default GroupFeed;
