if (process.env.NODE_ENV !== "production") require("dotenv").config();
const express = require("express");
const app = express();
var cors = require('cors'); 
app.use(cors);

const server = require("http").Server(app);
const io = require("socket.io")(server);
/*
const io = require("socket.io")(server,{
    cors: {
        origin: true,
        credentials: true
    },
})
*/

app.all('*', function(req, res, next) {
    //res.setHeader('Access-Control-Allow-Origin', 'https://umentorapp.herokuapp.com');
    res.header("Access-Control-Allow-Origin", req.headers.origin)
    res.setHeader('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-type,Accept,X-Access-Token,X-Key');
    res.header('Access-Control-Allow-Credentials', true);
    next()
})

const { ExpressPeerServer } = require("peer");
const peerServer = ExpressPeerServer(server, {
    debug: true
});

const {Users} = require("./peer/users");
const peerUser = new Users();

app.use(express.json());
app.use("/peerjs", peerServer);
app.use(express.urlencoded({ extended: false }));
app.use(express.static("public"));

app.get("/", (req, res) => {
    console.log("/user", req.query);
    res.json({
        error: "hello"
    });    
});

app.get("/user", async (req, res) => {
    console.log("/user", req.query);
    res.json({
        user: peerUser.findOne(req.query.peer)
    });
});

io.on("connection", (socket) => {
    socket.on("join-room",
           (roomId, peerId, userId, name, audio, video) => {
            peerUser.save({
                peerId: peerId,
                name: name,
                userId: userId,
                audio: audio,
                video: video,
            });


            socket.join(roomId);
            socket
                .to(roomId)
                .broadcast.emit(
                    "user-connected",
                    peerId,
                    name,
                    audio,
                    video,
                    0
                );

            socket.on("disconnect", () => {
                // remove peer details
                peerUser.deleteOne({ peerId: peerId });
                socket
                    .to(roomId)
                    .broadcast.emit(
                        "user-disconnected",
                        peerId,
                        0
                    );
            });
        }
    );
});

server.listen(process.env.PORT || 3000);
