import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  List,
  ListItem,
  ListItemAvatar,
  Avatar,
  ListItemText,
  Typography,
  Paper,
  Divider,
  Pagination,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { actionLoadUsers } from '../../store/Users';

const SearchUserPage: React.FC = () => {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const dispatch: any = useDispatch();
  const navigate = useNavigate();

  const users = useSelector((state: any) => state.users.users);
  const isLoading = useSelector((state: any) => state.users.loading);
  const totalUsers = useSelector((state: any) => state.users.totalCount);
  
  const usersPerPage = 4;

  const startIndex = (page - 1) * usersPerPage;
  const endIndex = startIndex + usersPerPage;
  const currentUsers = users.slice(startIndex, endIndex);

  const filteredUsers = users.filter((user: any) =>
    user.username.toLowerCase().includes(search.toLowerCase())
  );

  useEffect(() => {
    dispatch(actionLoadUsers(page-1,usersPerPage));
  }, [page, dispatch]);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(event.target.value);
    setPage(1);
  };

  const handleUserClick = (user: any) => {
    navigate(`/profile/${user.username}`);
  };

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
  };

  return (
    <Box sx={{ p: 3, backgroundColor: '#121212', minHeight: '100vh', paddingTop: '90px' }}>
      {/* Barra de pesquisa */}
      <Paper
        elevation={3}
        sx={{
          p: 2,
          mb: 4,
          backgroundColor: '#1e1e1e',
          borderRadius: 2,
        }}
      >
        <TextField
          label="Search user"
          variant="outlined"
          fullWidth
          value={search}
          onChange={handleSearchChange}
          sx={{
            input: { color: 'white' },
            label: { color: '#a0a0a0' },
            '& .MuiOutlinedInput-root': {
              '& fieldset': { borderColor: '#0579fc' },
              '&:hover fieldset': { borderColor: '#57a0fc' },
            },
          }}
        />
      </Paper>

      {/* Resultados */}
      {isLoading  && currentUsers.length === 0 ? (
        <Typography variant="h6" color="white" textAlign="center" sx={{ mt: 4 }}>
          Loading users...
        </Typography>
      ) : filteredUsers.length > 0 ? (
        <>
          <List>
            {currentUsers.map((user:any) => (
              <React.Fragment key={user.id}>
                <ListItem
                  onClick={() => handleUserClick(user)}
                  sx={{
                    '&:hover': { backgroundColor: '#1e1e1e' },
                  }}
                >
                  <ListItemAvatar>

                    <Avatar 
                      sx={{
                        border: '2px solid #0579fc',
                      }}
                      aria-label="recipe">

                        {user.username.charAt(0)}
                    </Avatar>
                    
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Typography color="white" variant="h6">
                        {user.username}
                      </Typography>
                    }
                  />
                </ListItem>
                <Divider sx={{ backgroundColor: '#0579fc' }} />
              </React.Fragment>
            ))}
          </List>
          <Pagination
            count={Math.ceil(totalUsers / usersPerPage)}
            page={page}
            onChange={handlePageChange}
            sx={{
              mt: 4,
              display: 'flex',
              justifyContent: 'center',
              '& .MuiPaginationItem-root': { color: 'white' },
            }}
          />
        </>
      ) : (
        <Typography
          variant="h6"
          color="white"
          textAlign="center"
          sx={{ mt: 4 }}
        >
          No users found.
        </Typography>
      )}
    </Box>
  );
};

export default SearchUserPage;
