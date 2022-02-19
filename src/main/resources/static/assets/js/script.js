const PORT = "3000";
//const SERVER_URL = "http://localhost:" + PORT;
const SERVER_URL = "https://umentorclass.herokuapp.com";
const HOST = SERVER_URL.replace(/(^\w+:|^)\/\//, '');

const socket = io(SERVER_URL);
const videoGrid = document.getElementById("video-grid");

const myPeer = new Peer(undefined, {
    path: "/peerjs",
    host: SERVER_URL.includes("localhost") ? "/" : HOST,
    port: SERVER_URL.includes("localhost") ? PORT : ""
});

var Peer_ID;
const myVideo = document.createElement("video");
myVideo.muted = true;
var myVideoStream;
var myVideoTrack;
const peers = {};

navigator.mediaDevices
    .getUserMedia({ audio: true })
    .then((stream) => {
        navigator.mediaDevices
            .getUserMedia({
                video: true,
                audio: true,
            })
            .then((stream) => {
                myVideoStream = stream;
                myVideoTrack = stream.getVideoTracks()[0];
                processStream(myVideoStream);
            }).catch((err) => {
                    console.log(err);
                    navigator.mediaDevices
                    .getUserMedia({
                        video: false,
                        audio: true,
                    })
                    .then((stream) => {
                        myVideoStream = stream;
                        processStream(myVideoStream);
                    });                    
                }
            );
    })
    .catch((err) => {
        navigator.mediaDevices
            .getUserMedia({
                video: true,
                audio: false,
            })
            .then((stream) => {
                myVideoStream = stream;
                processStream(myVideoStream);
            });
    });

socket.on("user-disconnected", (userId, count) => {
    console.log("user-disconnected", userId);
    if (peers[userId]) {
        peers[userId].close();
        delete peers[userId];
    }
});

function processStream(stream) {
    addVideoStream(myVideo, myVideoStream, null, {
        name: USER_NAME,
        audio: myVideoStream.getAudioTracks()[0] ? myVideoStream.getAudioTracks()[0].enabled : false,
        video: myVideoStream.getVideoTracks()[0] ? myVideoStream.getVideoTracks()[0].enabled : false,
        userId: USER_ID
    });
    // recieve the others stream
    myPeer.on("call", (call) => {
        peers[call.peer] = call;
        call.answer(myVideoStream);
        const video = document.createElement("video");
        call.on("stream", (userVideoStream) => {
            fetch(SERVER_URL+`/user?peer=${call.peer}&room=${ROOM_ID}`)
                .then((res) => {
                    return res.json();
                })
                .then((data) => {
                    console.log("user data", data.user);
                    addVideoStream(
                        video,
                        userVideoStream,
                        call.peer,
                        data.user
                    );
                });
        });
        call.on("close", () => {
            video.parentElement.remove();
        });
    });

    socket.on("user-connected", (userId, fname, audio, video, count) => {
        console.log("user-connected", fname);
        socket.emit("user-callback");
        connectToNewUser(userId, myVideoStream);
    });
}

myPeer.on("open", (id) => {
    Peer_ID = id;
    console.log("peer id is : ", id);
});

function connectToNewUser(userId, stream) {
    // set others peerid and send my stream
    const call = myPeer.call(userId, stream);
    const video = document.createElement("video");
    call.on("stream", (userVideoStream) => {
        console.log("call on stream : ", userVideoStream);
        fetch(SERVER_URL+`/user?peer=${call.peer}&room=${ROOM_ID}`)
            .then((res) => {
                return res.json();
            })
            .then((data) => {
                console.log("user data", data.user);
                addVideoStream(
                    video,
                    userVideoStream,
                    call.peer,
                    data.user,
                    data.admin
                );
            });
    });
    call.on("close", () => {
        video.parentElement.remove();
    });
    peers[userId] = call;
}

function addVideoStream(video, stream, peerId, user) {
    // create microphone button
    const micBtn = document.createElement("button");
    micBtn.classList.add("video-element");
    micBtn.classList.add("mic-button");
    micBtn.innerHTML = `<ion-icon name="mic-off-outline"></ion-icon>`;
    micBtn.classList.add("mic-off");

    // video off element
    const videoOffIndicator = document.createElement("div");
    videoOffIndicator.classList.add("video-off-indicator");
    videoOffIndicator.innerHTML = `<ion-icon name="videocam-outline"></ion-icon>`;

    // create pin button
    const pinBtn = document.createElement("button");
    pinBtn.classList.add("video-element");
    pinBtn.classList.add("pin-button");
    pinBtn.innerHTML = `<ion-icon name="expand-outline"></ion-icon>`;

    // main wrapper
    const videoWrapper = document.createElement("div");
    videoWrapper.id = "video-wrapper";
    videoWrapper.classList.add("video-wrapper");

    // peer id
    const imgTag = document.createElement("img");
    imgTag.src = '/profile/image/' + user.userId;
    imgTag.classList.add("video-element");
    imgTag.classList.add("user-avatar");

    // peer name
    const namePara = document.createElement("p");
    namePara.innerHTML = user.name;
    namePara.classList.add("video-element");
    namePara.classList.add("name");

    const elementsWrapper = document.createElement("div");
    elementsWrapper.classList.add("elements-wrapper");

    elementsWrapper.appendChild(imgTag);
    elementsWrapper.appendChild(namePara);
    elementsWrapper.appendChild(pinBtn);
    elementsWrapper.appendChild(micBtn);
    elementsWrapper.appendChild(videoOffIndicator);

    video.srcObject = stream;
    video.setAttribute("peer", peerId);
    video.setAttribute("name", user.name);

    if (peerId == null) {
        video.classList.add("mirror");
    }

    video.addEventListener("loadedmetadata", () => {
        video.play();
    });

    videoWrapper.appendChild(elementsWrapper);
    videoWrapper.appendChild(video);

    videoGrid.append(videoWrapper);

    const observer = new MutationObserver((mutationsList, observer) => {
        const removeNodeLength = mutationsList[0].removedNodes.length;
        const targetNode = mutationsList[0].target;
        if (removeNodeLength > 0) {
            targetNode.remove();
            observer.disconnect();
        }
    });
    observer.observe(videoWrapper, {
        childList: true,
    });
    eventAdd(pinBtn);
}

const eventAdd = (element) => {
    element.addEventListener("click", (e) => {
        const videoWrapper = e.target.parentElement.parentElement;
        if (e.target.childNodes[0].getAttribute("name") == "expand-outline") {
            e.target.innerHTML = `<ion-icon name="contract-outline"></ion-icon>`;
        } else {
            e.target.innerHTML = `<ion-icon name="expand-outline"></ion-icon>`;
        }
        videoWrapper.classList.toggle("zoom-video");
    });
};

// share screen
const shareScreenBtn = document.getElementById("share-screen");
shareScreenBtn.addEventListener("click", (e) => {
    if (e.target.classList.contains("true")) return;
    e.target.setAttribute("tool_tip", "You are already presenting screen");
    e.target.classList.add("true");
    navigator.mediaDevices
        .getDisplayMedia({
            video: true,
            audio: {
                echoCancellation: true,
                noiseSuppression: true,
                sampleRate: 44100,
            },
        })
        .then((stream) => {
            var videoTrack = stream.getVideoTracks()[0];
            myVideoTrack = myVideoStream.getVideoTracks()[0];
            replaceVideoTrack(myVideoStream, videoTrack);
            for (peer in peers) {
                let sender = peers[peer].peerConnection
                    .getSenders()
                    .find(function (s) {
                        return s.track.kind == videoTrack.kind;
                    });
                sender.replaceTrack(videoTrack);
            }
            const elementsWrapper = document.querySelector(".elements-wrapper");
            const stopBtn = document.createElement("button");
            stopBtn.classList.add("video-element");
            stopBtn.classList.add("stop-presenting-button");
            stopBtn.innerHTML = "Stop Sharing";
            elementsWrapper.classList.add("screen-share");
            elementsWrapper.appendChild(stopBtn);
            videoTrack.onended = () => {
                elementsWrapper.classList.remove("screen-share");
                stopBtn.remove();
                stopPresenting(videoTrack);
            };
            stopBtn.onclick = () => {
                videoTrack.stop();
                elementsWrapper.classList.remove("screen-share");
                stopBtn.remove();
                stopPresenting(videoTrack);
            };
        });
});

const stopPresenting = (videoTrack) => {
    shareScreenBtn.classList.remove("true");
    shareScreenBtn.setAttribute("tool_tip", "Present Screen");
    for (peer in peers) {
        let sender = peers[peer].peerConnection.getSenders().find(function (s) {
            return s.track.kind == videoTrack.kind;
        });
        sender.replaceTrack(myVideoTrack);
    }
    replaceVideoTrack(myVideoStream, myVideoTrack);
};

const crossBtnClickEvent = (e) => {
    const videoWrapper = e.target.parentElement;
    if (videoWrapper.classList.contains("zoom-video")) {
        videoWrapper.classList.remove("zoom-video");
        e.target.removeEventListener("click", crossBtnClickEvent);
        e.target.remove();
    }
};

const meetingToggleBtn = document.getElementById("meeting-toggle");
meetingToggleBtn.addEventListener("click", (e) => {
    const currentElement = e.target;
    if (currentElement.classList.contains("call-button")) {
        currentElement.classList.remove("call-button");
        currentElement.classList.add("call-end-button");
        currentElement.classList.add("tooltip-danger");
        currentElement.setAttribute("tool_tip", "Leave the Meeting");
        socket.emit(
            "join-room",
            ROOM_ID,
            Peer_ID,
            USER_ID,
            USER_NAME,
            myVideoStream.getAudioTracks()[0].enabled,
            myVideoStream.getVideoTracks()[0] ? myVideoStream.getVideoTracks()[0].enabled : false
        );                
    } else {
        socket.disconnect();
        location.replace(`/course-details/` + ROOM_ID);
    }
});

const replaceVideoTrack = (stream, videoTrack) => {
    stream.removeTrack(stream.getVideoTracks()[0]);
    stream.addTrack(videoTrack);
};

