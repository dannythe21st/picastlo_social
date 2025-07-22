import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Avatar,
} from '@mui/material';
import Grid from "@mui/material/Grid2";
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { useNavigate } from 'react-router-dom';
import { actionLogin } from '../../store/Users';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const dispatch = useDispatch();

  const handleLogin = async () => {
    try {
      console.log('Email:', email, 'Password:', password);
      
      await dispatch(actionLogin(email, password) as any);
      navigate('/feed');

    } catch (error) {
      console.error('Erro ao fazer login:', error);
      alert('Login falhou. Verifica as tuas credenciais.');
    }
  };



  return (
    <Box sx={{ p: 3, backgroundColor: '#121212', minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <Paper
        elevation={3}
        sx={{
          p: 4,
          maxWidth: 400,
          width: '100%',
          backgroundColor: '#1e1e1e',
          borderRadius: 2,
        }}
      >
        
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            mb: 3,
          }}
        >
          <Avatar
            sx={{
              backgroundColor: '#0579fc',
              mb: 1,
            }}
          >
            <LockOutlinedIcon />
          </Avatar>
          <Typography variant="h5" color="white">
            Login
          </Typography>
        </Box>

        
        <Grid container spacing={2}>
          <Grid size={{ xs: 12}}>
            <TextField
              label="Email"
              variant="outlined"
              fullWidth
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{
                input: { color: 'white' },
                label: { color: '#a0a0a0' },
                '& .MuiOutlinedInput-root': {
                  '& fieldset': { borderColor: '#0579fc' },
                  '&:hover fieldset': { borderColor: '#57a0fc' },
                },
              }}
            />
          </Grid>
          <Grid size={{ xs: 12}}>
            <TextField
              label="Password"
              type="password"
              variant="outlined"
              fullWidth
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              sx={{
                input: { color: 'white' },
                label: { color: '#a0a0a0' },
                '& .MuiOutlinedInput-root': {
                  '& fieldset': { borderColor: '#0579fc' },
                  '&:hover fieldset': { borderColor: '#57a0fc' },
                },
              }}
            />
          </Grid>
        </Grid>

        
        <Button
          variant="contained"
          fullWidth
          sx={{
            mt: 3,
            backgroundColor: '#0579fc',
            ':hover': {
              backgroundColor: '#0579fc',
              opacity: 0.9,
            },
          }}
          onClick={handleLogin}
        >
          Login
        </Button>

        
        <Typography
          variant="body2"
          color="#a0a0a0"
          textAlign="center"
          sx={{ mt: 2 }}
        >
          Don't have an account?{' '}
          <Typography
            component="span"
            color="#0579fc"
            sx={{ cursor: 'pointer' }}
            onClick={() => console.log('Redirect to sign up page')}
          >
            Sign up
            </Typography>
        </Typography>
      </Paper>
    </Box>
  );
};

export default LoginPage;
