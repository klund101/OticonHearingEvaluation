console.log("javascript initialized...");

	
var AudiogramCharter = function(element) {

    this.chart = 'div.charter';
	console.log(this.chart);
    this.xAxisTickValues = [125, 250, 500, 1000, 2000, 4000, 8000];
    this.yAxisTickValues = [0, 20, 40, 60, 80, 100, 120];
    this.yAxisGridTickValues = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120];
    this.sounds = [];
    this.snapInterval = 5;
    this.xPadding = 50;
    this.yPadding = 50;
    this.chartWidth = 600; // set resolution
    this.chartHeight = 500;
    this.dragged = this.selected = null;
    this.xScale = d3.scale.log().base(2)
        .domain([100, 10000])
        .rangeRound([this.xPadding, this.chartWidth - this.xPadding]);
    this.yScale = d3.scale.linear()
        .domain([0, 125])
        .rangeRound([this.yPadding, this.chartHeight - this.yPadding]);
    this.dataLeftEar = [
        [125, 0],
        [250, 0],
        [500, 0],
        [1000, 0],
        [2000, 0],
        [4000, 0],
        [8000, 0]
    ];
    this.dataRightEar = [
        [125, 0],
        [250, 0],
        [500, 0],
        [1000, 0],
        [2000, 0],
        [4000, 0],
        [8000, 0]
    ];
    this.svg = d3.select(this.chart)
        .append('svg')
        .attr('width', this.chartWidth)
        .attr('height', this.chartHeight);
    this.chartEarLayers = [];
    this.chartActiveLayer = '';
    this.tooltipWidth = 60;
    this.tooltipHeight = 30;
    this.rightEarDatapointRadius = 6;
    this.soundButtonRadius = 26;
    this.soundButtonPadding = 7;
    this.leftEarDataListeners = [];
    this.rightEarDataListeners = [];
    this.soundButtonClickListeners = [];
    this.speechSpectrumActive = false;
    this.speechSpectrumSrc = '';
    this.speechSpectrumLabel = '';
    this.buttonsActive = false;
    this.hearingLossActive = false;
    this.activeButtonId = '';
    this.leftEarDataPointImage = '';
    // d3.select(element)
        // .on("mousemove.drag", this.mousemove())
        // .on("touchmove.drag", this.mousemove())
        // .on("mouseup.drag", this.mouseup())
        // .on("touchend.drag", this.mouseup())
        // .on("mousedown", this.mousedown())
        // .on("touchstart", this.mousedown())
        // .on("mouseleave", this.mouseup())
        // .on("touchend", this.mouseup())
        // .on('click', this.event_killer());
};

AudiogramCharter.prototype.update = function() {
    var self = this;
    self.svg.selectAll(".audiogram-layer").remove();

    var makeContextArea = function() {
        return d3.svg.area()
            .interpolate('linear')
            .x(function(data) {
                return self.xScale(data[0]);
            })
            .y0(self.yPadding)
            .y1(function(data) {
                return self.yScale(data[1]);
            });
    };

    for (var i = 0; i < self.chartEarLayers.length; i++) {
        switch (self.chartEarLayers[i]) {
            case 'left':
                var leftEarLayer = self.svg.append('g').attr('class', 'audiogram-layer');

                var leftEarArea = leftEarLayer.selectAll("path.area")
                    .data([self.dataLeftEar]);

                leftEarArea.enter().append("path")
                    .attr("class", "audiogram-leftear-area")
                    .attr("d", makeContextArea());

                // Drawing Left Ear Data Points
                var leftEarDataPoints = leftEarLayer.selectAll('img')
                    .data(self.dataLeftEar);

                var dataPointsRef = leftEarDataPoints.enter().append('image')
                    .attr('xlink:href', self.leftEarDataPointImage)
                    .attr("transform", function(d) {
                        return "translate( -7, -8)";
                    })
                    .attr('x', function(data) {
                        return self.xScale(data[0]);
                    })
                    .attr('y', function(data) {
                        return self.yScale(data[1]);
                    })
                    .attr('class', 'audiogram-leftear-datapoint')
                    .attr("width", "16")
                    .attr("height", "17");

                // Toggling event handlers on placement in array
                if (i == (self.chartEarLayers.length - 1)) {
                    dataPointsRef
                        .on("mousedown.drag", self.datapoint_drag())
                        .on("touchstart.drag", self.datapoint_drag())
                        .on("mouseover", self.mouseover())
                        .on("mouseout", self.mouseout());
                }

                leftEarDataPoints.exit().remove();
                leftEarArea.exit().remove();

                // Notify data listeners
                for (var l = 0; l < self.leftEarDataListeners.length; l++) {
                    self.leftEarDataListeners[l](self.dataLeftEar);
                }

                break;
            case 'right':
                var rightEarLayer = self.svg.append('g').attr('class', 'audiogram-layer');

                var rightEarArea = rightEarLayer.selectAll("path.area")
                    .data([self.dataRightEar]);

                rightEarArea.enter().append("path")
                    .attr("class", "audiogram-rightear-area")
                    .attr("d", makeContextArea());

                // Drawing Right Ear Data Points
                var rightEarDataPoints = rightEarLayer.selectAll('circle')
                    .data(self.dataRightEar);

                var dataPointsRef = rightEarDataPoints.enter().append('circle')
                    .attr('cx', function(data) {
                        return self.xScale(data[0]);
                    })
                    .attr('cy', function(data) {
                        return self.yScale(data[1]);
                    })
                    .attr('r', self.rightEarDatapointRadius)
                    .attr('class', 'audiogram-rightear-datapoint');

                // Toggling event handlers on placement in array
                if (i == (self.chartEarLayers.length - 1)) {
                    dataPointsRef
                        .on("mousedown.drag", self.datapoint_drag())
                        .on("touchstart.drag", self.datapoint_drag())
                        .on("mouseover", self.mouseover())
                        .on("mouseout", self.mouseout());
                }

                rightEarDataPoints.exit().remove();
                rightEarArea.exit().remove();

                // Notify data listeners
                for (var r = 0; r < self.rightEarDataListeners.length; r++) {
                    self.rightEarDataListeners[r](self.dataRightEar);
                }

                break;
            default:
                //DO NOTHING
        }
    }

    // If tooltip is showing, make sure it's on top
    if (self.toolTipShowing) {
        self.hide_tooltip();
        self.show_tooltip();
    }

    // Make sure that buttons, if present, are on top
    if (self.buttonsActive) {
        self.remove_buttons();
        self.draw_buttons(self.sounds, self.hearingLossActive, self.activeButtonId);
    }

    if (d3.event) {
        d3.event.preventDefault();
        d3.event.stopPropagation();
    }
};

AudiogramCharter.prototype.click_sound_button = function () {
    var self = this;
    return function(buttondata, index) {
        var buttonid = "sound_" + index;
        if (self.activeButtonId == buttonid) {
            self.activeButtonId = '';
        } else {
            self.activeButtonId = buttonid;
        }

        // Notify button listeners
        for (var l = 0; l < self.soundButtonClickListeners.length; l++) {
            self.soundButtonClickListeners[l](self.activeButtonId);
        }

        if (self.buttonsActive) {
            self.remove_buttons();
            self.draw_buttons(self.sounds, self.hearingLossActive, self.activeButtonId);
        }
    };
};

AudiogramCharter.prototype.mousedown = function() {
    var self = this;
    return function() {
        self.mouseIsDown = true;
    };
};

AudiogramCharter.prototype.click = function () {
    var self = this;
    return function() {
        self.mousedown()();
        self.mousemove()();
        self.mouseup()();
    };
};

AudiogramCharter.prototype.datapoint_drag = function() {
    var self = this;
    return function(d) {
        document.onselectstart = function() {
            return false;
        };
        self.selected = self.dragged = d;
        self.update();
        self.hide_tooltip();
        self.show_tooltip(d);
    };
};

AudiogramCharter.prototype.mousemove = function() {
    var self = this;
    return function() {
        var p = d3.mouse(self.svg[0][0]),
            t = d3.event.changedTouches;

        if (self.dragged) {
            self.dragged[1] = Math.round(self.yScale.invert(Math.max(0, Math.min(self.chartHeight-self.yPadding, p[1]))) / self.snapInterval) * self.snapInterval;
            self.update();
        }

        if (self.mouseIsDown) {
            var bisectX = d3.bisector(function(data) {
                return data[0];
            }).left;
            var mouse = d3.mouse(self.svg.node());
            var mouseX = self.xScale.invert(mouse[0]);
            var mouseY = self.yScale.invert(mouse[1]);
            var index;
            var d0;
            var d1;
            var d;

            switch (self.chartEarLayers[self.chartEarLayers.length - 1]) {
                case 'left':
                    // Returns the index of the current data item
                    index = bisectX(self.dataLeftEar, mouseX);
                    d0 = self.dataLeftEar[index - 1];
                    d1 = self.dataLeftEar[index];

                    // Find closest X value to the mouse
                    if (!d0) {
                        d = d1;
                    } else if (!d1) {
                        d = d0;
                    } else {
                        d = mouseX - d0[0] > d1[0] - mouseX ? d1 : d0;
                    }
                    index = self.dataLeftEar.indexOf(d);
                    self.dataLeftEar[index][1] = Math.round(self.yScale.invert(Math.max(0, Math.min(self.chartHeight-self.yPadding, p[1]))) / self.snapInterval) * self.snapInterval;
                    self.hide_tooltip();
                    self.show_tooltip(self.dataLeftEar[index]);
                    break;
                case 'right':
                    // Returns the index of the current data item
                    index = bisectX(self.dataRightEar, mouseX);
                    d0 = self.dataRightEar[index - 1];
                    d1 = self.dataRightEar[index];

                    // Find closest X value to the mouse
                    if (!d0) {
                        d = d1;
                    } else if (!d1) {
                        d = d0;
                    } else {
                        d = mouseX - d0[0] > d1[0] - mouseX ? d1 : d0;
                    }
                    index = self.dataRightEar.indexOf(d);
                    self.dataRightEar[index][1] = Math.round(self.yScale.invert(Math.max(0, Math.min(self.chartHeight-self.yPadding, p[1]))) / self.snapInterval) * self.snapInterval;
                    self.hide_tooltip();
                    self.show_tooltip(self.dataRightEar[index]);
                    break;
                default:

            }
            self.update();
        }
    };
};

AudiogramCharter.prototype.mouseup = function() {
    var self = this;
    return function() {
        document.onselectstart = function() {
            return true;
        };
        d3.select('body').style("cursor", "auto");
        d3.select('body').style("cursor", "auto");
        if (self.dragged) {
            self.dragged = null;
        }
        if (self.mouseIsDown) {
            self.mouseIsDown = false;
        }
        self.hide_tooltip();
    };
};

AudiogramCharter.prototype.show_tooltip = function (datapoint) {
    var self = this;
    self.toolTipShowing = true;
    if (datapoint === undefined) {
        if (self.latestTooltipDatapoint !== undefined) {
            self.show_tooltip(self.latestTooltipDatapoint);
        }
    } else {
        self.latestTooltipDatapoint = datapoint;

        var tooltipText = datapoint[1] * -1 + " dB";

        var tooltipLayer = self.svg.append('g')
            .attr('class', 'audiogram-chart-tooltip');

        var tooltipRect = tooltipLayer.append('rect')
            .attr('width', self.tooltipWidth)
            .attr('height', self.tooltipHeight)
            .attr('x', function() {
                return self.xScale(datapoint[0]) - self.tooltipWidth/2;
            })
            .attr('y', function() {
                if (datapoint[1] < 0) {
                    return self.yScale(datapoint[1]) + self.tooltipHeight;
                } else {
                    return self.yScale(datapoint[1]) -  self.tooltipHeight - 15;
                }
            })
            .attr('class', 'audiogram-chart-tooltip-container');

        tooltipLayer.append('text')
            .text(tooltipText)
            .attr('x', function() {
                return self.xScale(datapoint[0]);
            })
            .attr('y', function() {
                if (datapoint[1] < 0) {
                    return self.yScale(datapoint[1]) + self.tooltipHeight * 1.7;
                } else {
                    return self.yScale(datapoint[1]) - self.tooltipHeight *0.8;
                }
            })
            .attr('class', 'audiogram-chart-tooltip-text');
    }
};

AudiogramCharter.prototype.hide_tooltip = function () {
    var self = this;
    self.toolTipShowing = false;
    self.svg.selectAll('.audiogram-chart-tooltip').remove();
};

AudiogramCharter.prototype.mouseover = function () {
    var self = this;
    return function (data) {
        self.show_tooltip(data);
    };
};

AudiogramCharter.prototype.mouseout = function () {
    var self = this;
    return function() {
        self.hide_tooltip();
    };
};

AudiogramCharter.prototype.event_killer = function () {
    // Event listener used to kill events, so they don't bleed through to other SVG elements
    return function () {
        d3.event.preventDefault();
        d3.event.stopPropagation();
    };
};

AudiogramCharter.prototype.redraw = function() {
    var self = this;
	console.log("redraw_init");
    /** RECALULATE SCALES **/
    self.xScale = d3.scale.log().base(2)
        .domain([100, 10000])
        .rangeRound([self.xPadding, self.chartWidth - self.xPadding]);
    self.yScale = d3.scale.linear()
        .domain([0, 125])
        .rangeRound([self.yPadding, self.chartHeight - self.yPadding]);

    /** REDRAW ROOT SVG **/
    d3.selectAll('svg').remove();
    self.svg = d3.select(self.chart)
        .append('svg')
        .attr('width', self.chartWidth)
        .attr('height', self.chartHeight);


    /** AXES **/
    var makeXAxis = function() {
        return d3.svg.axis()
            .scale(self.xScale)
            .orient('bottom')
            .tickValues(self.xAxisTickValues)
            .tickFormat(d3.format("s"))
            .tickSize(-self.chartHeight);
    };

    var makeYAxis = function() {
        return d3.svg.axis()
            .scale(self.yScale)
            .orient('left')
            .tickValues(self.yAxisTickValues)
            .tickFormat(d3.format("s"))
            .tickSize(-self.chartWidth + 2 * self.yPadding);
    };

    var makeYGridAxis = function() {
        return d3.svg.axis()
            .scale(self.yScale)
            .orient('left')
            .tickValues(self.yAxisGridTickValues)
            .tickFormat("")
            .tickSize(-self.chartWidth + 2 * self.yPadding, 0, 0);
    };

    /** SVG DEFINITIONS **/
    // Layers
    var chartLayer = self.svg.append('g');

    // Drawing speech spectrum
    if (self.speechSpectrumActive) {
        chartLayer.append('image')
            .attr('xlink:href', self.speechSpectrumSrc)
            .attr('x', self.xScale(100))
            .attr('y', self.yScale(-15))
            .attr('width', function() {
                return self.xScale(10000) - self.xScale(100);
            })
            .attr('height', function() {
                return self.yScale(90)-self.yScale(0);
            })
            .attr('class', 'audiogram-chart-speech-spectrum');

        chartLayer.append('text')
            .text(self.speechSpectrumLabel)
            .attr('class', 'audiogram-chart-speech-spectrum-label')
            .attr('transform', 'rotate(-90), translate(-' + self.yScale(40) + ',' + self.xScale(12000) + ')');
    }

    // Drawing extra grid
    chartLayer.append("g")
        .attr("class", "grid")
        .attr("transform", "translate(" + self.xPadding + "," + 0 + ")")
        .call(makeYGridAxis());

    //Drawing axes
    chartLayer.append("g")
        .attr('class', 'axis')
        .attr('transform', 'translate(0,' + (self.chartHeight - self.xPadding) + ')')
        .call(makeXAxis())
        .selectAll("text")
        .style("text-anchor", "middle")
        .attr("dy", "1.75em");

    chartLayer.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(' + self.yPadding + ',0)')
        .call(makeYAxis())
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em");

    // Drawing extra chart lines
    chartLayer.append('line')
        .attr('x1', self.yPadding)
        .attr('y1', 0)
        .attr('x2', self.chartWidth - self.xPadding)
        .attr('y2', 0)
        .attr('class', 'top-axis');

    var verticalDashedDataPoints = [750, 1500, 3000, 6000];
    for (var i = 0; i < verticalDashedDataPoints.length; i++) {
        var dataPoint = verticalDashedDataPoints[i];
        chartLayer.append('line')
            .attr('x1', function() {
                return self.xScale(dataPoint);
            })
            .attr('y1', 0)
            .attr('x2', function() {
                return self.xScale(dataPoint);
            })
            .attr('y2', self.chartHeight - self.yPadding)
            .attr('class', 'vertical-dotted')
            .style('stroke-dasharray', ('2,4'));
    }

    //Drawing extra labels
    chartLayer.append('text')
        .text('dB HL')
        .attr('class', 'dbhl')
        .attr('x', 0)
        .attr('y', 12);

    chartLayer.append('text')
        .text('Hz')
        .attr('x', self.chartWidth - self.xPadding)
        .attr('y', self.chartHeight - self.yPadding / 2.5);

    self.update();

    /** SOME BLING **/
    self.apply_shadows(self.svg, 1, 0, 0.5);
};

AudiogramCharter.prototype.draw_buttons = function (sounds, hearing_loss_active, active_sound_index) {
    var self = this;
    self.sounds = sounds;
    self.hearingLossActive = hearing_loss_active;
    self.activeButtonId = active_sound_index;
    self.buttonsActive = true;
    var buttonLayer = self.svg.append('g')
        .attr('class', 'audiogram-sound-button-layer');
    var buttons = buttonLayer.selectAll('.sound-button')
        .data(self.sounds)
        .enter().append('g');

    buttons.append('circle')
        .attr('cx', function(data) {
            return self.xScale(data.fields.x_position);
        })
        .attr('cy', function(data) {
            return self.yScale(data.fields.y_position);
        })
        .attr('r', self.soundButtonRadius)
        .attr('class', function(data, index) {
            var styleClass =  "audiogram-sound-button button-" + index;
            var soundid = "sound_" + index;
            if (active_sound_index == soundid) {
                styleClass += " sound-button-clicked";
            }
            if (hearing_loss_active) {
                styleClass += " sound-button-hearing-loss-active";
            }
            return styleClass;
        })
        .on('click', self.click_sound_button())
        .on("mousemove.drag", self.event_killer())
        .on("touchmove.drag", self.event_killer())
        .on("mouseup.drag", self.event_killer())
        .on("touchend.drag", self.event_killer())
        .on("mousedown", self.event_killer())
        .on("touchstart", self.click_sound_button())
        .on("mouseleave", self.event_killer())
        .on("touchend", self.event_killer());

    buttons.append('image')
        .attr('height', self.soundButtonRadius*2 - self.soundButtonPadding)
        .attr('width',  self.soundButtonRadius*2 - self.soundButtonPadding)
        .attr('x', function(data) {
            return self.xScale(data.fields.x_position) - self.soundButtonRadius + self.soundButtonPadding/2;
        })
        .attr('y', function(data) {
            return self.yScale(data.fields.y_position) - self.soundButtonRadius + self.soundButtonPadding/2 ;
        })
        .attr('class', function(data) {
            return "audiogram-sound-button-image";
        })
        .attr('id', function(data, index) {
            return "sound-"+index;
        })
        .attr('xlink:href', function(data, index) {
            var soundid = "sound_" + index;
            if (active_sound_index == soundid) {
                return data.fields.icon_clicked;
            } else if(hearing_loss_active) {
                return data.fields.icon_on;
            } else {
                return data.fields.icon_off;
            }
        })
        .on('click', self.click_sound_button())
        .on("mousemove.drag", self.event_killer())
        .on("touchmove.drag", self.event_killer())
        .on("mouseup.drag", self.event_killer())
        .on("touchend.drag", self.event_killer())
        .on("mousedown", self.event_killer())
        .on("touchstart", self.click_sound_button())
        .on("mouseleave", self.event_killer())
        .on("touchend", self.event_killer());
};

AudiogramCharter.prototype.remove_buttons = function () {
    var self = this;
    self.buttonsActive = false;
    self.svg.selectAll(".audiogram-sound-button-layer").remove();
};

AudiogramCharter.prototype.draw_speech_spectrum = function (speech_spectrum_src, speech_spectrum_label) {
    var self = this;

    self.speechSpectrumSrc = speech_spectrum_src;
    self.speechSpectrumLabel = speech_spectrum_label;
    self.speechSpectrumActive = true;
    self.redraw();
};

AudiogramCharter.prototype.remove_speech_spectrum = function () {
    var self = this;
    self.speechSpectrumActive = false;
    self.svg.selectAll('.audiogram-chart-speech-spectrum').remove();
    self.svg.selectAll('.audiogram-chart-speech-spectrum-label').remove();
};

AudiogramCharter.prototype.apply_shadows = function(svg, blurDeviation, offsetX, offsetY) {
    var self = this;
    // filters go in defs element
    var defs = svg.append("defs");

    // create filter with id #drop-shadow
    // height=130% so that the shadow is not clipped
    var filter = defs.append("filter")
        .attr("id", "drop-shadow")
        .attr('y', '-50%')
        .attr('x', '-50%')
        .attr("height", "200%")
        .attr("width", "200%");

    // SourceAlpha refers to opacity of graphic that this filter will be applied to
    // convolve that with a Gaussian with standard deviation 3 and store result
    // in blur
    filter.append("feGaussianBlur")
        .attr("in", "SourceAlpha")
        .attr("stdDeviation", blurDeviation)
        .attr("result", "blur");

    // translate output of Gaussian blur to the right and downwards with 2px
    // store result in offsetBlur
    filter.append("feOffset")
        .attr("in", "blur")
        .attr("dx", offsetX)
        .attr("dy", offsetY)
        .attr("result", "offsetBlur");

    // overlay original SourceGraphic over translated blurred opacity by using
    // feMerge filter. Order of specifying inputs is important!
    var feMerge = filter.append("feMerge");

    feMerge.append("feMergeNode")
        .attr("in", "offsetBlur");
    feMerge.append("feMergeNode")
        .attr("in", "SourceGraphic");
};

var charter = new AudiogramCharter();
charter.redraw();
//console.log("redraw success!");
window.alert("chart javascript finalized");