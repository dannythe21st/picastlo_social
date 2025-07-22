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
import { actionLoadGroups } from '../../store/Groups';

const GroupsPage: React.FC = () => {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const dispatch: any = useDispatch();
  const navigate = useNavigate();

  const groups = useSelector((state: any) => state.groups.groups);
  const isLoading = useSelector((state: any) => state.groups.loading);
  const totalGroups = useSelector((state: any) => state.groups.totalCount);

  const groupsPerPage = 3;


  const startIndex = (page - 1) * groupsPerPage;
  const endIndex = startIndex + groupsPerPage;
  const currentGroups = groups.slice(startIndex, endIndex);

  const filteredGroups = groups.filter((group: any) =>
    group.name.toLowerCase().includes(search.toLowerCase())
  );

  useEffect(() => {
    dispatch(actionLoadGroups(page-1,groupsPerPage));
  }, [page, dispatch]);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(event.target.value);
    setPage(1);
  };

  const handleUserClick = (group: any) => {
    navigate(`/group/${group.id}`);
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
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
          label="Search group on page"
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
      {isLoading && currentGroups.length === 0 ? (
        <Typography variant="h6" color="white" textAlign="center" sx={{ mt: 4 }}>
          Loading groups...
        </Typography>
      ) : filteredGroups.length > 0 ? (
        <>
          <List>
            {currentGroups.map((group:any) => (
              <React.Fragment key={group.id}>
                <ListItem
                  onClick={() => handleUserClick(group)}
                  sx={{
                    '&:hover': { backgroundColor: '#1e1e1e' },
                  }}
                >
                  <ListItemAvatar>
                    <Avatar 
                    sx={{
                        border: '2px solid #0579fc',
                      }}aria-label="recipe">
                        {group.name.charAt(0)}
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Typography color="white" variant="h6">
                        {group.name}
                      </Typography>
                    }
                    secondary={
                      <Typography color="white" variant="body1">
                        Owner: {group.owner}
                      </Typography>
                    }
                  />
                </ListItem>
                <Divider sx={{ backgroundColor: '#0579fc' }} />
              </React.Fragment>
            ))}
          </List>
          <Pagination
            count={Math.ceil(totalGroups / groupsPerPage)}
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
          No groups found.
        </Typography>
      )}
    </Box>
  );
};

export default GroupsPage;
