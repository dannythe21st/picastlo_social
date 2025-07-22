import React, { useState, useEffect } from "react";
import {
  Box,
  Avatar,
  Button,
  Divider,
  Typography,
  Paper,
  Dialog,
  IconButton,
  TextField,
  InputAdornment,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  CircularProgress,
  Tooltip,
  Pagination,
  Fab,
} from "@mui/material";
import Grid from "@mui/material/Grid2";
import CloseIcon from "@mui/icons-material/Close";
import SearchIcon from "@mui/icons-material/Search";
import DownloadIcon from "@mui/icons-material/Download";
import OpenInBrowserIcon from "@mui/icons-material/OpenInBrowser";
import LaunchIcon from "@mui/icons-material/Launch";
import AddIcon from "@mui/icons-material/Add";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { actionLoadFriends } from "../../store/Friends";
import { actionLoadMyPosts, actionLoadUserPosts, clearPosts } from "../../store/Post";
import PostCard from "../Post/PostCard";
import { actionGetPipelines } from "../../store/Pipeline";
import { PostDTO } from "../../api";

const ProfilePage: React.FC = () => {
  const dispatch: any = useDispatch();

  const [username, setUsername] = useState("username");
  const { id } = useParams<{ id: string }>();
  const [search, setSearch] = useState("");
  const [friendsModalOpen, setFriendsModalOpen] = useState(false);
  const [pipelinesModalOpen, setPipelinesModalOpen] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();

  const handleOpenFriendsModal = () => setFriendsModalOpen(true);
  const handleCloseFriendsModal = () => setFriendsModalOpen(false);

  const handleOpenPipelinesModal = () => setPipelinesModalOpen(true);
  const handleClosePipelinesModal = () => setPipelinesModalOpen(false);

  const friends = useSelector((state: any) => state.friends.friends);
  const friendsLoading = useSelector((state: any) => state.friends.loading);

  const posts = useSelector((state: any) => state.posts.posts);
  const postsLoading = useSelector((state: any) => state.posts.loading);

  const pipelines = useSelector((state: any) => state.pipelines.pipelines);
  const pipelinesLoading = useSelector((state: any) => state.pipelines.loading);

  const totalPosts = useSelector((state: any) => state.posts.totalCount);
  const [page, setPage] = useState(1);

  const postsPerPage = 6;
  const startIndex = (page - 1) * postsPerPage;
  const endIndex = startIndex + postsPerPage;
  const currentPosts = posts.slice(startIndex, endIndex);

  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    value: number
  ) => {
    setPage(value);
  };

  const [myOwnPage, setMyOwnPage] = useState(true);

  useEffect(() => {
    if (id === undefined) {
      setUsername(localStorage.getItem("username") || "");
      setMyOwnPage(true);
    } else {
      setUsername(id);
      setMyOwnPage(false);
    }
  }, [id, location]);

  useEffect(() => {
    if (id === undefined || id === username) {
      dispatch(actionLoadMyPosts(page - 1, postsPerPage));
    } else {
      dispatch(actionLoadUserPosts(username, page - 1, postsPerPage));
    }
  }, [page, username, myOwnPage, dispatch]);

  useEffect(() => {
    dispatch(clearPosts())
  }, [id]);

  useEffect(() => {
    if (myOwnPage) dispatch(actionLoadFriends(username));
    else dispatch(actionLoadFriends(username));
  }, [username, myOwnPage, friendsModalOpen]);

  useEffect(() => {
    if (myOwnPage) dispatch(actionGetPipelines(username));
    else dispatch(actionGetPipelines(username));
  }, [username, myOwnPage, pipelinesModalOpen]);

  const filteredFriends = friends.filter((friend: any) =>
    friend.toLowerCase().includes(search.toLowerCase())
  );

  const filteredPipelines = pipelines.filter((pipeline: any) =>
    pipeline?.name.toLowerCase().includes(search.toLowerCase())
  );

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(event.target.value);
  };

  const handleUserClick = (user: any) => {
    navigate(`/profile/${user}`);
    handleCloseFriendsModal()
  };

  const handleOpenPicastloGUI = (pipelineId: number) => {
    const pipeline = pipelines.find((p: any) => p.id === pipelineId);
    localStorage.setItem(
      "pipeline2Picastlo",
      JSON.stringify(pipeline.transformations)
    );
    if (pipeline.originalImage > 0) {
      localStorage.setItem("image2Picastlo", pipeline.originalImage);
    } else {
      localStorage.removeItem("image2Picastlo");
    }
    navigate(`/picastlo/loadedPipeline`);
  };

  const handleOpenPipelineInNewTab = (pipelineId: number) => {
    const pipeline = pipelines.find((p: any) => p.id === pipelineId);
    if (pipeline) {
      const newWindow = window.open("", "_blank");
      if (newWindow) {
        newWindow.document.write(`<pre>${pipeline.transformations}</pre>`);
      }
    } else {
      console.error("Pipeline not found");
    }
  };

  const handleDownloadPipeline = (pipelineId: number) => {
    const pipeline = pipelines.find((p: any) => p.id === pipelineId);
    if (pipeline) {
      const blob = new Blob([pipeline.transformations], {
        type: "application/json",
      });

      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `${pipeline.name}_transformations.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } else {
      console.error("Pipeline not found");
    }
  };

  return (
    <Box
      sx={{
        p: 3,
        backgroundColor: "#121212",
        minHeight: "100vh",
        paddingTop: "90px",
      }}
    >
      <Paper
        elevation={3}
        sx={{
          p: 3,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          mb: 4,
          backgroundColor: "#1e1e1e",
          borderRadius: 2,
        }}
      >
        <Avatar
          sx={{
            width: 120,
            height: 120,
            mb: 2,
            border: "3px solid #0579fc",
          }}
        >
          {username.charAt(0).toUpperCase()}
        </Avatar>

        <Typography variant="h5" fontWeight="bold" color="white">
          {username}
        </Typography>
        <Grid container spacing={3} sx={{ width: "100%", marginTop: "2vh" }}>
          <Grid
            size={6}
            display="flex"
            justifyContent="right"
            alignItems="center"
          >
            <Button
              variant="outlined"
              onClick={handleOpenFriendsModal}
              sx={{
                color: "#ffffff",
                borderColor: "#0579fc",
                ":hover": {
                  backgroundColor: "#0579fc",
                  color: "#ffffff",
                },
              }}
            >
              Friends list
            </Button>
          </Grid>
          <Grid
            size={6}
            display="flex"
            justifyContent="left"
            alignItems="center"
          >
            <Button
              variant="outlined"
              onClick={handleOpenPipelinesModal}
              sx={{
                color: "#ffffff",
                borderColor: "#0579fc",
                ":hover": {
                  backgroundColor: "#0579fc",
                  color: "#ffffff",
                },
              }}
            >
              My Pipelines
            </Button>
          </Grid>
        </Grid>
      </Paper>

      <Divider sx={{ mb: 3, backgroundColor: "#0579fc" }} />

      {postsLoading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>
          <CircularProgress />
        </Box>
      ) : posts.length === 0 ? (
        <Typography sx={{ color: "white" }}>No posts found.</Typography>
      ) : (
        <Grid container spacing={3} >
          {currentPosts.map((post: PostDTO) => (
            <Grid
              size={{ xs: 12, sm: 6, md: 4 }}
              sx={{paddingLeft:"30px" }}
              key={post.id}
            >
              <PostCard post={post} avatarVis={false} />
            </Grid>
          ))}
        </Grid>
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
          onChange={handlePageChange}
          sx={{
            mt: 4,
            display: "flex",
            justifyContent: "center",
            "& .MuiPaginationItem-root": { color: "white" },
          }}
        />
      </Box>

      <Tooltip title="Create New Post">
        <Fab
          color="primary"
          sx={{
            position: "fixed",
            bottom: 30,
            right: 40,
            backgroundColor: "#313131",
            ":hover": {
              backgroundColor: "#1e1e1e",
            },
          }}
        >
          <AddIcon sx={{ color: "#c9c9c9" }} />
        </Fab>
      </Tooltip>

      <Dialog open={friendsModalOpen} onClose={handleCloseFriendsModal}>
        <Paper
          sx={{
            p: 2,
            backgroundColor: "#1e1e1e",
            color: "white",
            borderRadius: 2,
            position: "relative",
            height: "100%",
          }}
        >
          <IconButton
            onClick={handleCloseFriendsModal}
            sx={{ position: "absolute", right: 8, top: 8 }}
          >
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" fontWeight="bold" sx={{ mb: 2 }}>
            Friends List
          </Typography>

          <TextField
            fullWidth
            variant="outlined"
            placeholder="Search friends..."
            value={search}
            onChange={handleSearchChange}
            sx={{
              backgroundColor: "#2e2e2e",
              borderRadius: 1,
              mb: 2,
            }}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              },
            }}
          />
          {friendsLoading ? (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>
              <CircularProgress />
            </Box>
          ) : friends.length === 0 ? (
            <Typography sx={{ mt: 2 }}>No friends found.</Typography>
          ) : (
            <List sx={{ mt: 2 }}>
              {filteredFriends.map((friend: any) => (
                <ListItem
                  key={friend.id}
                  onClick={() => handleUserClick(friend)}
                  sx={{ cursor: "pointer" }}
                >
                  <ListItemAvatar>
                    <Avatar aria-label="recipe">{friend.charAt(0)}</Avatar>
                  </ListItemAvatar>
                  <ListItemText primary={friend} />
                </ListItem>
              ))}
            </List>
          )}
        </Paper>
      </Dialog>

      <Dialog
        open={pipelinesModalOpen}
        onClose={handleClosePipelinesModal}
        maxWidth="sm"
        fullWidth
        sx={{
          "& .MuiDialog-paper": {
            width: "50vw",
            maxWidth: "75vw",
            height: "70vh",
            maxHeight: "90vh",
            overflow: "hidden",
          },
        }}
      >
        <Paper
          sx={{
            p: 2,
            backgroundColor: "#1e1e1e",
            color: "white",
            borderRadius: 2,
            position: "relative",
            height: "100%",
          }}
        >
          <IconButton
            onClick={handleClosePipelinesModal}
            sx={{
              position: "absolute",
              top: 8,
              right: 8,
              color: "white",
            }}
          >
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" fontWeight="bold" sx={{ mb: 2 }}>
            My Pipelines
          </Typography>

          <TextField
            fullWidth
            variant="outlined"
            placeholder="Search pipelines..."
            value={search}
            onChange={handleSearchChange}
            sx={{
              backgroundColor: "#2e2e2e",
              borderRadius: 1,
              mb: 2,
            }}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              },
            }}
          />
          {pipelinesLoading ? (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>
              <CircularProgress />
            </Box>
          ) : pipelines.length === 0 ? (
            <Typography sx={{ mt: 2 }}>No pipelines found.</Typography>
          ) : (
            <List
              sx={{
                maxHeight: "60vh",
                overflowY: "auto",
                paddingBottom: "10vh",
              }}
            >
              {filteredPipelines.map((pipeline: any) => (
                <ListItem
                  key={pipeline.id}
                  sx={{
                    backgroundColor: "#2e2e2e",
                    borderRadius: 1,
                    mb: 1,
                  }}
                >
                  <Tooltip title={pipeline.description} arrow>
                    <ListItemText
                      primary={pipeline.name}
                      primaryTypographyProps={{
                        sx: {
                          fontSize: "1.1rem",
                          fontWeight: "bold",
                        },
                      }}
                      secondary={
                        pipeline.originalImage > 0
                          ? `Original Image: ${pipeline.originalImage}`
                          : "No image"
                      }
                      sx={{
                        color: "white",
                        "& .MuiListItemText-secondary": {
                          display: "-webkit-box",
                          WebkitBoxOrient: "vertical",
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          WebkitLineClamp: 2,
                          maxHeight: "3em",
                        },
                      }}
                    />
                  </Tooltip>

                  <Box sx={{ display: "flex", gap: 1 }}>
                    <Tooltip title="Download">
                      <IconButton
                        onClick={() => handleDownloadPipeline(pipeline.id)}
                        sx={{ color: "white" }}
                      >
                        <DownloadIcon />
                      </IconButton>
                    </Tooltip>

                    <Tooltip title="Open in new tab">
                      <IconButton
                        onClick={() => handleOpenPipelineInNewTab(pipeline.id)}
                        sx={{ color: "white" }}
                      >
                        <OpenInBrowserIcon />
                      </IconButton>
                    </Tooltip>

                    <Tooltip title="Open in Picastlo GUI">
                      <IconButton
                        onClick={() => handleOpenPicastloGUI(pipeline.id)}
                        sx={{ color: "white" }}
                      >
                        <LaunchIcon />
                      </IconButton>
                    </Tooltip>
                  </Box>
                </ListItem>
              ))}
            </List>
          )}
        </Paper>
      </Dialog>
    </Box>
  );
};

export default ProfilePage;
