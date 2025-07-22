import './App.css';
import { Provider, useDispatch } from 'react-redux'
import { store } from './store';
import ResponsiveAppBar from './components/AppBar/ResponsiveAppBar';
import Feed from './components/Feed/Feed';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import ProfilePage from './components/PersonalArea/MyProfile';
import SearchUserPage from './components/Users/SearchUsers';
import LoginPage from './components/Login/LoginPage';
import GroupsPage from './components/Groups/Groups';
import Picastlo from './picastloGUI/Picastlo';
import { logIn } from './store/Auth';
import GroupFeed from './components/Groups/GroupFeed';


const darkTheme = createTheme({
  palette: {
    mode: 'dark',
  },
});

function App() {
  const dispatch: any = useDispatch();

  var token = localStorage.getItem("authToken");
  if(token!=null && localStorage.getItem("username")!=null){
    console.log("USER IS AUTHENTICATED")
    dispatch(logIn())
  }

  return (

    <ThemeProvider theme={darkTheme}>
      <CssBaseline />
    
      <Router>
        <ResponsiveAppBar/>
        <Routes>
          <Route path="/" element={<Navigate to="/feed" replace />} />
          <Route path="/feed" element={<Feed />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/profile/:id" element={<ProfilePage />} />
          <Route path="/search" element={<SearchUserPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/picastlo" element={<Picastlo />} />
          <Route path="/picastlo/loadedPipeline" element={<Picastlo />} />
          <Route path="/groups" element={<GroupsPage />} />
          <Route path="/group/:id" element={<GroupFeed />} />
        </Routes>
      </Router>
        
    </ThemeProvider>
  );
}

const RdxApp = () => 
  <Provider store={store}>
    <App/>
  </Provider>

export default RdxApp;
