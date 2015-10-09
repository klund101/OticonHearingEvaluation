var title = "audiogram";

var tpl = '\
<div class="audiogram-wrapper">\
    <div class="audiogram-loadscreen">\
        <div class="audiogram-load-progress-wrapper">\
            <div class="audiogram-load-progress"></div>\
            <h2 class="audiogram-load-percentage core-label-list_title"></h2>\
        </div>\
    </div>\
    <div class="audiogram-presentation-wrapper">\
        <div class="audiogram-chart" class="chart">\
        </div>\
    </div>\
    <div class="audiogram-navigation-wrapper">\
        <div class="audiogram-selection-wrapper">\
            <table>\
                <tr>\
                    <td class="audiogram-selection-checkbox">\
                        <button data-bind="click: $root.toggleActiveEar.bind($data,\'left\')">\
                            <img data-bind="attr:{src:$root.dataModel.fields.checkbox_unchecked}" />\
                            <img class="checkmark" data-bind="visible: ($root.activeEars.indexOf(\'left\') != -1), attr:{src: $root.dataModel.fields.left_ear_checkbox_checked}" />\
                        </button>\
                    </td>\
                    <td>\
                        <h3 class="core-label-button" data-bind="text: $root.dataModel.fields.left_ear_label"></h3>\
                    </td>\
                    <td>\
                        <button data-bind="click: $root.setActiveEarEdit.bind($data,\'left\')">\
                            <img data-bind=" attr:{src:($root.activeEars()[$root.activeEars().length - 1] == \'left\')?$root.dataModel.fields.left_ear_edit_button_active:$root.dataModel.fields.edit_button_inactive}" />\
                        </button>\
                    </td>\
                </tr>\
                <tr>\
                    <td class="audiogram-selection-checkbox">\
                        <button data-bind="click: $root.toggleActiveEar.bind($data,\'right\')">\
                            <img data-bind="attr:{src:$root.dataModel.fields.checkbox_unchecked}" />\
                            <img class="checkmark" data-bind="visible: ($root.activeEars.indexOf(\'right\') != -1), attr:{src: $root.dataModel.fields.right_ear_checkbox_checked}" />\
                        </button>\
                    </td>\
                    <td>\
                        <h3 class="core-label-button" data-bind="text: $root.dataModel.fields.right_ear_label"></h3>\
                    </td>\
                    <td>\
                        <button data-bind="click: $root.setActiveEarEdit.bind($data,\'right\')">\
                            <img data-bind=" attr:{src:($root.activeEars()[$root.activeEars().length - 1] == \'right\')?$root.dataModel.fields.right_ear_edit_button_active:$root.dataModel.fields.edit_button_inactive}" />\
                        </button>\
                    </td>\
                </tr>\
            </table>\
        </div>\
        <button class="audiogram-reset-button core-label-square_button" name="button" data-bind="click: $root.resetAudiogram()">\
            <h3 class="core-label-button" data-bind="text: dataModel.fields.reset_label"></h3>\
        </button>\
        <hr class="audiogram-navigation-divider core-list-border_color">\
        <div class="audiogram-selection-wrapper">\
            <table>\
                <tr>\
                    <td class="audiogram-selection-checkbox">\
                        <button data-bind="click: $root.toggleSpeech">\
                            <img data-bind=" attr:{src:$root.dataModel.fields.checkbox_unchecked}" />\
                            <img class="checkmark" data-bind="visible: $root.speechActive(), attr:{src:$root.dataModel.fields.speech_checkbox_checked}" />\
                        </button>\
                    </td>\
                    <td>\
                        <h3 class="core-label-button" data-bind="text: $root.dataModel.fields.speech_label"></h3>\
                    </td>\
                </tr>\
                <tr>\
                    <td class="audiogram-selection-checkbox">\
                        <button data-bind="click: $root.toggleSoundExamples">\
                            <img data-bind=" attr:{src:$root.dataModel.fields.checkbox_unchecked}" />\
                            <img class="checkmark" data-bind="visible: $root.soundExamplesActive(), attr:{src:$root.dataModel.fields.speech_checkbox_checked}" />\
                        </button>\
                    </td>\
                    <td>\
                        <h3 class="core-label-button" data-bind="text: $root.dataModel.fields.sound_examples_label"></h3>\
                    </td>\
                </tr>\
                <tr data-bind="visible: $root.speechActive() || $root.soundExamplesActive()">\
                    <td class="audiogram-selection-checkbox">\
                        <button data-bind="click: $root.toggleHearingLoss">\
                            <img data-bind=" attr:{src:$root.dataModel.fields.checkbox_unchecked}" />\
                            <img class="checkmark" data-bind="visible: $root.hearingLossActive(), attr:{src:$root.dataModel.fields.speech_checkbox_checked}" />\
                        </button>\
                    </td>\
                    <td>\
                        <h3 class="core-label-button" data-bind="text: $root.dataModel.fields.simulate_hearing_loss_label"></h3>\
                    </td>\
                </tr>\
            </table>\
        </div>\
        <hr class="audiogram-navigation-divider core-list-border_color" data-bind="visible: $root.speechActive()">\
        <div class="audiogram-speech-selection-wrapper" data-bind="visible: $root.speechActive()">\
            <table>\
                <tr data-bind="foreach: $root.dataModel.speech_list">\
                    <td>\
                        <img class="speech-actor" data-bind="attr:{src: $data.fields.image}, click: $root.playPause.bind($data, (\'speech_\' + $index())), css: {activespeech: $root.activeSound() == (\'speech_\' + $index())}" />\
                    </td>\
                </tr>\
                <tr data-bind="foreach: $root.dataModel.speech_list">\
                    <td>\
                        <div class="audiogram-speech-sound-button" data-bind="click: $root.playPause.bind($data, (\'speech_\' + $index()))">\
                            <img data-bind="attr:{src:($root.activeSound() == (\'speech_\' + $index()))?$root.dataModel.fields.sound_button_pause:($root.hearingLossActive()?$root.dataModel.fields.sound_button_play:$root.dataModel.fields.sound_button_inactive)}" />\
                        </div>\
                    </td>\
                </tr>\
            </table>\
        </div>\
    </div>\
</div>';

var ins = {};

var outs = {};

var init = function(model, container) {
    var dataModel = {
        fields: {},
        speech_list: [],
        sound_list: []
    };

    var retrieveFieldData = function(field) {
        if (field.hasOwnProperty("mediapath")) {
            return api.assets.getPath(field.mediapath);
        } else if (field.hasOwnProperty("cdata")) {
            return field.cdata;
        } else if (field.hasOwnProperty("numbervalue")) {
            return field.numbervalue;
        }
    }

    var formatList = function(enginelist) {
        var formattedList = [];
        for (var i = 0; i < enginelist.length; i++) {
            formattedList[i] = {
                fields: {},
                sounds: {}
            };
            for (var field in enginelist[i].fields) {
                if (enginelist[i].fields.hasOwnProperty(field)) {
                    formattedList[i].fields[field] = retrieveFieldData(enginelist[i].fields[field]);
                }
            }
            for (var c = 0; c < enginelist[i].lists.length; c++) {
                var soundType = enginelist[i].lists[c];
                formattedList[i].sounds[soundType.fields.band_khz.fieldvalue] = {
                    db: soundType.fields.db_adjustment.fieldvalue,
                    src: retrieveFieldData(soundType.fields.sound)
                };
            }
        }
        return formattedList;
    }

    for (var field in model.fields) {
        if (model.fields.hasOwnProperty(field)) {
            dataModel.fields[field] = retrieveFieldData(model.fields[field]);
        }
    }

    for (var i = 0; i < model.lists.length; i++) {
        switch (model.lists[i].name) {
            case "speech_list":
                dataModel.speech_list = formatList(model.lists[i].lists);
                break;
            case "sound_list":
                dataModel.sound_list = formatList(model.lists[i].lists);
                break;
            default:
                console.log("Unknown list");
        }
    }

    function AudiogramViewModel() {
        var self = this;
        self.dataModel = dataModel;
        self.presentationWidth = ko.observable($('.audiogram-presentation-wrapper').width());
        self.presentationHeight = ko.observable( $('.audiogram-presentation-wrapper').height());
        self.charter = new AudiogramCharter('div.audiogram-chart');
        self.charter.sounds = self.dataModel.sound_list;
        self.charter.leftEarDataPointImage = self.dataModel.fields.graph_cross;
        self.chartAspect = 1.05;
        self.audioEngine = new AudioEngine(); //audioEngine; //new AudioEngine();
        self.activeEars = ko.observableArray();
        self.activeEarEdit = ko.observable();
        self.activeSound = ko.observable();
        self.speechActive = ko.observable(false);
        self.soundExamplesActive = ko.observable(false);
        self.hearingLossActive = ko.observable(false);
        self.loadPercentage = 0;
	self.loadedSounds = 0;
        self.neutralEQ = function() {return [
               [125, 0],
               [250, 0],
               [500, 0],
               [1000, 0],
               [2000, 0],
               [4000, 0],
               [8000, 0]
            ];};
        self.activeEars.subscribe(function() {
            self.syncChart();
        });

        self.presentationWidth.subscribe(function() {
            self.refitChart();
        });

        self.presentationHeight.subscribe(function() {
            self.refitChart();
        });

        self.toggleActiveEar = function(data) {
            if (self.activeEars.indexOf(data) == -1) {
                self.activeEars.push(data);
            } else {
                self.activeEars.remove(data);
            }
        };

        self.setActiveEarEdit = function(data) {
            if (self.activeEars()[self.activeEars().length - 1] != data) {
                self.activeEars.reverse();
                self.activeEarEdit(data);
                self.syncChart();
            }
        };

        self.syncChart = function() {
            self.charter.chartEarLayers.length = self.activeEars().length;
            for (var i = 0; i < self.activeEars().length; i++) {
                self.charter.chartEarLayers[i] =
                 self.activeEars()[i];
            }

            self.charter.update();
        };

        self.refitChart = function() {
            if (self.presentationWidth() < self.presentationHeight()*self.chartAspect) {
                self.charter.chartWidth = self.presentationWidth();
                self.charter.chartHeight = self.presentationWidth()/self.chartAspect;
            }

            if (self.presentationHeight() < self.presentationWidth()/self.chartAspect) {
                self.charter.chartHeight = self.presentationHeight();
                self.charter.chartWidth = self.presentationHeight()*self.chartAspect;
            }

            self.charter.redraw();
        };

        self.resetAudiogram = function() {
            self.charter.dataLeftEar = self.neutralEQ();
            self.charter.dataRightEar = self.neutralEQ();
            self.charter.update();
        };

        self.playPause = function(data) {
            if (self.activeSound() == data) {
                self.activeSound(undefined);
                self.audioEngine.stop_all_bands();
            } else {
                self.audioEngine.stop_all_bands();
                if (data.indexOf("speech") != -1) {
                    self.charter.remove_buttons();
                    if (self.soundExamplesActive()) {
			self.charter.draw_buttons( self.dataModel.sound_list, self.hearingLossActive(), '');
		    }
                }
                self.activeSound(data);
                self.audioEngine.play_all_bands(data);
            }
        };

        self.leftEarDataHandler = function(leftEarData) {
            for (var i = 0; i < leftEarData.length; i++) {
                var band = leftEarData[i][0];
                var db = leftEarData[i][1] * -1;
                self.audioEngine.set_band_db(band, db, 'left');
            }
        };

        self.rightEarDataHandler = function(rightEarData) {
            for (var i = 0; i < rightEarData.length; i++) {
                var band = rightEarData[i][0];
                var db = rightEarData[i][1] * -1;
                self.audioEngine.set_band_db(band, db, 'right');
            }
        };

        self.soundButtonClickHandler = function(buttonId) {
            self.playPause(buttonId);
        };

        self.toggleSpeech = function() {
            self.speechActive(!self.speechActive());
            if (self.speechActive()) {
                self.charter.draw_speech_spectrum(self.dataModel.fields.speech_spectrum, self.dataModel.fields.speech_spectrum_label);
            } else {
                self.charter.remove_speech_spectrum();
            }
        };

        self.toggleHearingLoss = function() {
            self.hearingLossActive(!self.hearingLossActive());
            switch (self.hearingLossActive()) {
                case false:
                    var leftIndex = self.charter.leftEarDataListeners.indexOf(self.leftEarDataHandler);
                    var rightIndex = self.charter.rightEarDataListeners.indexOf(self.rightEarDataHandler);
                    self.charter.leftEarDataListeners.splice(leftIndex,1);
                    self.charter.rightEarDataListeners.splice(rightIndex,1);
                    self.leftEarDataHandler(self.neutralEQ());
                    self.rightEarDataHandler(self.neutralEQ());
                    self.charter.hearingLossActive = false;
                    break;
                case true:
                    self.charter.leftEarDataListeners.push(self.leftEarDataHandler);
                    self.charter.rightEarDataListeners.push(self.rightEarDataHandler);
                    self.charter.hearingLossActive = true;
                    break;
                default:
            }

            self.charter.update();
        };

        self.toggleSoundExamples = function() {
            self.soundExamplesActive(!self.soundExamplesActive());
            if (self.soundExamplesActive()) {
                self.charter.draw_buttons( self.dataModel.sound_list, self.hearingLossActive());
                self.charter.soundButtonClickListeners.push(self.soundButtonClickHandler);
            } else {
                self.charter.remove_buttons();
                var listenerIndex = self.charter.soundButtonClickListeners.indexOf(self.soundButtonClickHandler);
                self.charter.soundButtonClickListeners.splice(listenerIndex,1);
            }
        };

        self.soundLoadHandler = function() {
	    self.audioEngine.filesLoaded++;
            var percentage = Math.ceil(self.audioEngine.filesLoaded/self.audioEngine.totalFiles*100);;
            $('.audiogram-load-progress').animate({
                width: percentage + "%"
            }, 100, "linear");
            $('.audiogram-load-percentage').html(percentage + "%");

            if (self.audioEngine.filesLoaded >= self.audioEngine.totalFiles) {
                $('.audiogram-loadscreen').animate({
                    opacity: 0.0,
                    zIndex: -100
                },1000,"linear");
            }
        };
    }

    /** VIEWMODEL BINDING **/
    var audiogramVM = new AudiogramViewModel();
    ko.applyBindings(audiogramVM, $(".audiogram-wrapper:last").get(0));
    audiogramVM.refitChart();

    /** LISTEN TO WINDOW RESIZE **/
    $(window).on("resize", function() {
        audiogramVM.presentationWidth($('.audiogram-presentation-wrapper').width());
        audiogramVM.presentationHeight( $('.audiogram-presentation-wrapper').height());
    });


    /** REGISTERSOUND  **/
    var soundlist = {}
    for (var i = 0; i < audiogramVM.dataModel.sound_list.length; i++) {
        soundlist["sound_"+i] = audiogramVM.dataModel.sound_list[i].sounds;
    }
    var totalSpeechCount = 0;
    for (var i = 0; i < audiogramVM.dataModel.speech_list.length; i++) {
        soundlist["speech_"+i] = audiogramVM.dataModel.speech_list[i].sounds;
    }
    
    audiogramVM.audioEngine.load_sounds(soundlist, audiogramVM.soundLoadHandler);

    //Draw chart
    audiogramVM.charter.redraw();
};

api.template.add(title, tpl, init, ins, outs);
