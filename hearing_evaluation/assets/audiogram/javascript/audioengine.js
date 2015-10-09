var AudioEngine = function() {
    this.sounds = {};
    this.filesLoaded = 0;
    this.totalFiles = 0;
    this.dbMin = -125;
    this.dbMax = 10;
    this.volumeMax = 1;
    this.volumeMin = 0;
    this.channels = {
        left: {
            125: {
                dbCurrent: 0,
                instance: undefined
            },
            250: {
                dbCurrent: 0,
                instance: undefined
            },
            500: {
                dbCurrent: 0,
                instance: undefined
            },
            1000: {
                dbCurrent: 0,
                instance: undefined
            },
            2000: {
                dbCurrent: 0,
                instance: undefined
            },
            4000: {
                dbCurrent: 0,
                instance: undefined
            },
            8000: {
                dbCurrent: 0,
                instance: undefined
            }
        },
        right: {
            125: {
                dbCurrent: 0,
                instance: undefined
            },
            250: {
                dbCurrent: 0,
                instance: undefined
            },
            500: {
                dbCurrent: 0,
                instance: undefined
            },
            1000: {
                dbCurrent: 0,
                instance: undefined
            },
            2000: {
                dbCurrent: 0,
                instance: undefined
            },
            4000: {
                dbCurrent: 0,
                instance: undefined
            },
            8000: {
                dbCurrent: 0,
                instance: undefined
            }
        }
    };
    this.soundIdCurrent = "";
};

AudioEngine.prototype.load_sounds = function(sounds, load_handler) {
    var self = this;
    self.sounds = sounds;
    var loadableSoundlist = [];

    for (var soundId in sounds) {
        if (sounds.hasOwnProperty(soundId)) {
            for (var band in sounds[soundId]) {
                if (sounds[soundId].hasOwnProperty(band)) {
                    loadableSoundlist.push({
                        src: sounds[soundId][band].src,
                        id: soundId + "_" + band
                    });
                    self.totalFiles++;
                }
            }
        }
    }

    createjs.Sound.on("fileload", load_handler, self);
    createjs.Sound.registerSounds(loadableSoundlist, "");
};

AudioEngine.prototype.load_handler = function(event) {
    var self = this;
    self.filesLoaded++;
    console.log(self.filesLoaded + " files loaded, out of " + self.totalFiles);
};

AudioEngine.prototype.remove_all_event_listeners = function() {
    createjs.Sound.removeAllEventListeners();
};

AudioEngine.prototype.play_all_bands = function(soundId) {
    var self = this;
    self.soundIdCurrent = soundId;
    var timeToPlay = (new Date()).getTime() + 100; // Time to play multiple
    // sounds in sync.
    for (var band in self.sounds[soundId]) {
        var time = timeToPlay - (new Date()).getTime();
        setTimeout(function(b) {
            return function() {
                //Start left channel
                self.channels.left[b].instance = createjs.Sound.play(soundId + "_" + b, {
                    loop: -1
                });
                self.channels.left[b].instance.pan = -1;
                //Start right channel
                self.channels.right[b].instance = createjs.Sound.play(soundId + "_" + b, {
                    loop: -1
                });
                self.channels.right[b].instance.pan = 1;
                //Adjust to standard band db
                self.set_to_remembered_db(b);

            };
        }(band), time);
    }
};

AudioEngine.prototype.stop_all_bands = function() {
    createjs.Sound.stop();
};

AudioEngine.prototype.db_to_volume = function(db) {
    var self = this;
    var newdB = (parseFloat(db) - parseFloat(self.dbMax));
    // switch (self.soundIdCurrent) {
    //     case 'sound_0': // Airplane
    //         if (newdB > -50) {
    //             newdB += -20;
    //         } else if (newdB < -145) {
    //             newdB += -50;
    //         } else {
    //             newdB += 50;
    //         }
    //         break;
    //     case 'sound_1': // Bird
    //         newdB += -50;
    //         break;
    //     case 'sound_2': // Bus
    //         newdB += -30;
    //         break;
    //     case 'sound_3': // Dog
    //         newdB += -30;
    //         break;
    //     case 'sound_4': // Tree
    //         newdB += -12;
    //         break;
    //     case 'sound_5': // Phone
    //         newdB += -13;
    //         break;
    //     case 'sound_6': // Water
    //         newdB += -15;
    //         break;
    //     case 'speech_0':
    //         newdB += -40;
    //         break;
    //     case 'speech_1':
    //         newdB += -40;
    //         break;
    //     case 'speech_2':
    //         newdB += -40;
    //         break;
    //     case 'Kitchen':
    //         newdB += -40;
    //         break;
    //     case 'Car':
    //         newdB += -40;
    //         break;
    //     case 'Living Room':
    //         newdB += -40;
    //         break;
    //     case 'Meeting Room':
    //         newdB += -40;
    //         break;
    //     case 'Restaurant':
    //         newdB += -40;
    //         break;
    //     default:
    //         break;
    // }
    // return Math.exp(newdB/20);
    var newVol = Math.pow(10, newdB/50);
    // var newVol = (1 - newdB / self.dbMin) * self.volumeMax;
    return newVol;
};

// AudioEngine.prototype.db_to_volume = function (db) {
//     var self = this;
//     var ucl = self.dbMax;
//
//     // if(ucl - db < 0) {
//     //     ucl = db + 10;
//     // }
//
//     var factor = (ucl - db) / ucl;
//     var newdB = ucl - (ucl/factor);
//     return newdB;
// };

AudioEngine.prototype.set_to_remembered_db = function(band, channel) {
    var self = this;
    if (channel) {
        self.set_band_db(band, self.channels[channel][band].dbCurrent, channel);
    } else {
        self.set_band_db(band, self.channels['left'][band].dbCurrent, 'left');
        self.set_band_db(band, self.channels['right'][band].dbCurrent, 'right');
    }

};

AudioEngine.prototype.set_band_db = function(band, db, channel) {
    var self = this;
    var basedb = 0;
    if (self.soundIdCurrent !== '') {
        basedb = self.sounds[self.soundIdCurrent][band].db;
    }
    var setdb = parseFloat(db);
    var dbResult = parseFloat(basedb) + setdb;
    switch (channel) {
        case 'left':
            self.channels.left[band].dbCurrent = setdb;
            if (self.channels.left[band].instance !== undefined) {
                self.channels.left[band].instance.volume = self.db_to_volume(dbResult);
            }
            break;
        case 'right':
            self.channels.right[band].dbCurrent = setdb;
            if (self.channels.right[band].instance !== undefined) {
                self.channels.right[band].instance.volume = self.db_to_volume(dbResult);
            }
            break;
        default:
            self.set_band_db(band, db, 'left');
            self.set_band_db(band, db, 'right');
    }
};
