/**
 * 產生台灣行政區地圖
 * 
 * @param chartsData
 *            建立報表的資料
 * @param webRootPath
 *            專案路徑
 * @param divId
 *            欲顯示的區塊物件ID 格式："#testDiv"
 * @param cityName
 *            縣市名稱
 */
 function getDistrictCharts(chartsData, webRootPath, divId, cityName) {
	/***************************************************************************
	 * 注意事項： 1.新竹縣市需獨立拉出來區分 2.台中市跟高雄市需把小行政區拉出來
	 **************************************************************************/
	if (!webRootPath == "" && !divId == "" && !cityName == "") {
		
		/** ---- 基本設定 START ---- * */
		cityName = cityName.replace('台','臺');
		var cityNameSecond = false;
		if(cityName == '新竹縣市') {
			cityName = '新竹市';
			cityNameSecond = '新竹縣';
		}
		
		$(divId).empty();
		var width = 623, height = 532; 
		
		var svg = d3.select(divId)
		.append("svg")
		.attr("width", width)
		.attr("height", height)
		
		var g = svg.append("g");
		/** ---- 基本設定 END ---- * */
		
		/** ---- 撈取縣市 START ---- * */
		var districtData = [];
		$.ajax({
			dataType : "json",
			url : webRootPath + "/city.json",
			async : false,
			success : function(data) {
				$.each(data, function(i,item) {
					if(cityName == i.replace('台','臺')){
						$.each(item,function(d){
							districtData.push(d);
						});
					} else if(cityNameSecond == i.replace('台','臺')) {
						$.each(item,function(d){
							districtData.push(d);
						});
					}
				});
			}
		});
		/** ---- 撈取縣市 END ---- * */
		
		/** ---- 繪製地圖 START ---- * */
		
		/** ---- 取得縣市的經緯度及縮放大小 ---- * */
		var lon, lat, scale;
		$.ajax({
			dataType: "json",
			url: webRootPath + "/topojson/townscenter.json",
			async: false,
			success: function (centerData) {
				var dataKey = Object.keys(centerData.objects);
				
				// 取得縣市的縮放位置
				dataKey.map(function (i) {
					if (cityName == centerData.objects[i]["COUNTYNAME"].replace('台','臺')) {
						lon = parseFloat(centerData.objects[i]["COUNTYLON"]);
						lat = parseFloat(centerData.objects[i]["COUNTYLAT"]);
						scale = parseInt(centerData.objects[i]["COUNTYSCALE"]);
					}
				});
			}
		});
		
		var projectmethod = d3.geoMercator().center([lon, lat]).scale(scale); // 設定經緯及縮放大小
		var pathGenerator = d3.geoPath().projection(projectmethod);
		
		/** ---- 正式繪製地圖 ---- * */
		d3.json(webRootPath + "/topojson/taiwantowns.json").then(function (data) {
			var geometries = topojson.feature(data, data.objects["towns"]).features; // 取得台灣所有地區座標
			var districtArray = [];
			
			// 將所選地區拉出來
			geometries.map(function (d) {
				if (cityName == d.properties["COUNTYNAME"].replace('台','臺')) {
					districtArray.push(d);
				} else if(cityNameSecond) {
					if (cityNameSecond == d.properties["COUNTYNAME"].replace('台','臺')) {
						districtArray.push(d);
					}
				}
			})
			
			g.selectAll("path")
			.data(districtArray)
			.enter()
			.append("path")
			.attr("d", pathGenerator)
			.classed("county-area", true)
			.attr("id", function (d) { return d.properties["TOWNNAME"]})
			
			var splitDistrict = taiwanSplitData(); // 設定需切割的行政區
			var fullDistrict = ["東區","北區","中區","西區","南區","中西區"]; // 不需刪的區域
			var tmpArray = []; // 若切割有的話就加進去
			
			$.each(splitDistrict,function(key,value){
				if (cityName == key.replace('台','臺')) {
					tmpArray = splitDistrict[key];
				}
			})
			
			/*******************************************************************
			 * ******重要******* 開始繪製 1.行政區 2.座標點 3.座標線
			 ******************************************************************/
			
			g.selectAll('text')
			.data(districtArray)
			.enter()
			.append("text")
			.attr("id", function (d) { return d.properties["TOWNNAME"] + "-text" })
			.attr('x', function (d) { return pathGenerator.centroid(d)[0] })
			.attr('y', function (d) { return pathGenerator.centroid(d)[1] - 9 })
			.text(function (d) {
				if(tmpArray.includes(d.properties["TOWNNAME"])){
					return "";
				} else if(fullDistrict.includes(d.properties["TOWNNAME"])){
					return d.properties["TOWNNAME"];
				} else{
					return d.properties["TOWNNAME"].replace("區", "");
				}
			})
			.attr("class", "county-location")
			.append("tspan")
			.attr('x', function (d) { return pathGenerator.centroid(d)[0] })
			.attr('y', function (d) { return pathGenerator.centroid(d)[1] })
			.attr("dy","0.3em")
			.attr("font-size",40)
			.style("fill", "#000000")
			.style("text-shadow", "0 0 black")
			.text(function (d) {
				
				if(tmpArray.includes(d.properties["TOWNNAME"])){
					return "";
				}
				
				return "‧";
			});
				
			if (tmpArray.length > 0) {
				getDistrictCharts_split(chartsData, webRootPath, divId, cityName, geometries, tmpArray);
			} 
			
			// 更改顏色
			var map_count = chartsData.get("map_count");
			var map_color = chartsData.get("map_color");

			for(var i = 0; i<districtData.length ; i++){
				var chkColor = chartsData.has(districtData[i]) ? chartsData.get(districtData[i]) : 0 ;
				
				for(var loop = 0 ; loop < map_count.length ; loop ++ ){
					if(loop != map_count.length - 1){
						if(chkColor > map_count[loop] && chkColor <= map_count[loop+1]){
							console.log(map_count[loop] + "-" + map_count[loop+1]);
							d3.select("path#"+districtData[i]).classed("county-area",false).style("fill",map_color[loop+1]).style("stroke","gray");
							d3.select("path#"+districtData[i]+"-small").classed("county-area",false).style("fill",map_color[loop+1]).style("stroke","gray");
							d3.select("text#"+districtData[i]+"-text").classed("county-text-white",true);
							d3.select("text#"+districtData[i]+"-small-text").classed("county-text-white",true);
							d3.select("text#"+districtData[i]+"-text tspan").style("fill", "#FFFFFF").style("text-shadow","0 0 black");
							d3.select("text#"+districtData[i]+"-small-text tspan").style("fill", "#FFFFFF").style("text-shadow","0 0 black");
						}
					} else {
						if(chkColor > map_count[loop]){
							
							d3.select("path#"+districtData[i]).classed("county-area",false).style("fill",map_color[loop+1]).style("stroke","gray");
							d3.select("path#"+districtData[i]+"-small").classed("county-area",false).style("fill",map_color[loop+1]).style("stroke","gray");
							d3.select("text#"+districtData[i]+"-text").classed("county-text-white",true);
							d3.select("text#"+districtData[i]+"-small-text").classed("county-text-white",true);
							d3.select("text#"+districtData[i]+"-text tspan").style("fill", "#FFFFFF").style("text-shadow","0 0 black");
							d3.select("text#"+districtData[i]+"-small-text tspan").style("fill", "#FFFFFF").style("text-shadow","0 0 black");
						}
					}
				}
			}
		})
		/** ---- 繪製地圖 END ---- * */
	}
}

/**
 * 
 * @param webRootPath
 * @param divId
 * @param cityName
 * @param geometries
 * @param tmpArray
 * @returns
 */
function getDistrictCharts_split(chartsData, webRootPath, divId, cityName, geometries, tmpArray) {
	
	if (cityName.replace('台','臺') == '臺中市'){
		var width = 200, height = 200;
		
		var svg = d3.select(divId)
		.append("svg")
		.attr("width", width)
		.attr("height", height)
		.attr("class", "county-area-taichung");
		
		var g = svg.append("g");
		
		var projectmethod = d3.geoMercator().center([120.86, 24.07]).scale(120000);
		var pathGenerator = d3.geoPath().projection(projectmethod);
		
	} else if (cityName == '高雄市') {
		var width = 250, height = 400;
		
		var svg = d3.select(divId)
		.append("svg")
		.attr("width", width)
		.attr("height", height)
		.attr("class", "county-area-kaohsiung");
		
		var g = svg.append("g");
		
		var projectmethod = d3.geoMercator().center([120.59, 22.58]).scale(80000).rotate([0,0]);
		var pathGenerator = d3.geoPath().projection(projectmethod);
	}
	
	var districtArray = [];
	
	// 將所選地區拉出來
	geometries.map(function (d) {
		if (cityName == d.properties["COUNTYNAME"].replace('台','臺')) {
			if(tmpArray.includes(d.properties["TOWNNAME"])) {
				districtArray.push(d);
			}
		}
	})
	
	g.selectAll("path")
	.data(districtArray)
	.enter()
	.append("path")
	.attr("d", pathGenerator)
	.classed("county-area", true)
	.attr("id", function (d) { return d.properties["TOWNNAME"] + "-small" })
	
	g.selectAll('text')
	.data(districtArray)
	.enter()
	.append("text")
	.attr("id", function (d) { return d.properties["TOWNNAME"] + "-small-text" })
	.attr('x', function (d) { return pathGenerator.centroid(d)[0] })
	.attr('y', function (d) { return pathGenerator.centroid(d)[1] - 10 })
	.text(function (d) {
		if (cityName.replace('台','臺') == '臺中市'){
			return d.properties["TOWNNAME"];
		} else{
			return d.properties["TOWNNAME"].replace("區","");
		}
	})
	.attr("class", "county-location")
	.append("tspan")
	.attr('x', function (d) { return pathGenerator.centroid(d)[0] })
	.attr("dy","0.6em")
	.attr("font-size",40)
	.style("fill", "#000000")
	.style("text-shadow", "0 0 black")	
	.text("‧");
}

function taiwanSplitData(){
	var splitDistrict = {
			"台中市" : ["中區","東區","西區","南區","北區"],
			"高雄市" : ["楠梓區","左營區","前金區","鼓山區","三民區","前鎮區","旗津區","小港區","苓雅區","新興區","鹽埕區"]
		}
	
	return splitDistrict;
}

function taiwanOutsideData(){
	var outsideDistrict = new Map([
		["阿蓮",{"x":340,"y":220}],
		["岡山",{"x":280,"y":250}],
		["路竹",{"x":200,"y":220}],
		["湖內",{"x":140,"y":220}],
		["茄萣",{"x":140,"y":250}],
		["橋頭",{"x":120,"y":290}],
		["永安",{"x":120,"y":310}],
		["楠梓",{"x":160,"y":330}],
		["彌陀",{"x":160,"y":360}],
		["梓官",{"x":160,"y":390}],
		["左營",{"x":140,"y":410}],
		["鼓山",{"x":160,"y":430}],
		["前鎮",{"x":160,"y":450}],
		["旗津",{"x":160,"y":480}],
		["小港",{"x":220,"y":480}],
		["林園",{"x":360,"y":480}],
		["大寮",{"x":360,"y":460}],
		["鳳山",{"x":400,"y":440}],
		["鳥松",{"x":400,"y":420}],
		["三民",{"x":400,"y":400}],
		["大樹",{"x":400,"y":380}],
		["仁武",{"x":400,"y":360}],
		["大社",{"x":400,"y":340}],
	]);
	
	return outsideDistrict;
}

function getMiddlePoint(startPoint, endPoint){
	
	// 起點的座標
	var startX = startPoint.x;
	var startY = startPoint.y + 9;
	// 終點座標
	var endX = endPoint.x;
	var endY = endPoint.y;
	
	if(startX > endX){
		endX += 20 ;
		if(startY - 50 > endY && startX - 50 < endX){	// 遙遠的左上方
			endY += 10 ;				
		}else if(startX - 100 > endX){	// 左方
			endY -= 5 ;
		}else{	// 小左
			endY -= 5 ;
		}
		
	}else if(startX < endX){
		if(startY - 50 > endY && startX + 50 < endX){	// 遙遠的右上方
			endX -= 15 ;
			endY += 5 ;
		}else if(startX + 100 < endX){	// 右方
			endX -= 20 ;
			endY -= 5 ;
		}else if(startY - 50 > endY){	// 上方
			endX -= 5 ;
			endY += 10 ;
		}else{	// 小右
			endX -= 20 ;
			endY -= 5 ;
		}
	}
	
	var calculateX = endX - startX;
	var calculateY = endY - startY;
	var tan = Math.atan(calculateY / calculateX);
	var len = Math.sqrt((calculateX * calculateX) + (calculateY * calculateY)) / 2 * 0.5;
	var calculateT = tan - Math.PI / 2;
	
	var middleX = (endX + startX) / 2 + len * Math.cos(calculateT);
	var middleY = (endY + startY) / 2 + len * Math.sin(calculateT);
	
	return "M" + startX + " " + startY + " Q " + middleX + " " + middleY + " " + endX + " " + endY;
}

function getChartObj(chartObj){
	if(!chartObj.series[1]){
		return "";
	}
	$.each(chartObj.series[0].data, function(i,point){
		
		/** 計算吃字 START **/
		var n_r = chartObj.series[0].data[i].dataLabel.y;
		var n_b = chartObj.series[1].data[i].dataLabel.y;
		
		while(Math.abs(n_r - n_b) < 10){
			chartObj.series[0].data[i].dataLabel.attr({ y:chartObj.series[0].data[i].dataLabel.y-5 });
			n_r = n_r - 5;
			chartObj.series[1].data[i].dataLabel.attr({ y:chartObj.series[1].data[i].dataLabel.y+5 });
			n_b = n_b + 5;
		}
		if(i != 0){
			var b_r = chartObj.series[0].data[i-1].dataLabel.y;
			var b_b = chartObj.series[1].data[i-1].dataLabel.y;
			
			while(Math.abs(n_r - b_b) < 10 || Math.abs(n_r - b_r) < 10){

				if(Math.abs(n_r - b_r) < 10){
					chartObj.series[0].data[i].dataLabel.attr({ y:chartObj.series[0].data[i].dataLabel.y-5 });
					n_r = n_r - 5;
				} else {
					chartObj.series[0].data[i].dataLabel.attr({ y:chartObj.series[0].data[i].dataLabel.y+5 });
					n_r = n_r + 5;
					chartObj.series[1].data[i].dataLabel.attr({ y:chartObj.series[1].data[i].dataLabel.y+5 });
					n_b = n_b + 5;
				}
				
			}
			while(Math.abs(n_r - n_b) < 10){
				chartObj.series[1].data[i].dataLabel.attr({ y:chartObj.series[1].data[i].dataLabel.y+5 });
				n_b = n_b + 5;
			}
			
			while(Math.abs(n_b - b_r) < 10 || Math.abs(n_b - b_b) < 10){
				chartObj.series[1].data[i].dataLabel.attr({ y:chartObj.series[1].data[i].dataLabel.y+5 });
				n_b = n_b + 5;
			}
		}
		/** 計算吃字 END **/
	})
}
