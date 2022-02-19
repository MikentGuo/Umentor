class Users {

  constructor () {
    this.users = [];
  }

  save (one) {
    var user = {
		        peerId: one.peerId,
		        name: one.name, 
            userId: one.userId,
		        audio: one.audio, 
		        video: one.video
		        };

    this.users.push(user);
    return user;
  }

  deleteOne (one) {
    var user = this.findOne(one.peerId);

    if (user) {
      this.users = this.users.filter((user) => user.peerId !== one.peerId);
    }

    return user;
  }

  findOne (id) {
    console.log("findOne:", id);
    var user = this.users.filter((user) => user.peerId === id)[0]
    console.log("findOne ret:", user)
    return user;
  }

  updateOne (id, option) {
	var user = this.findOne(id);
	if(user) {
		if(option.audio)
			user.audio = option.audio;
		if(option.video)
			user.video = option.video;
	}
  }
}

module.exports = {Users};