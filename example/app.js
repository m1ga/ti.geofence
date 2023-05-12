import ti_geofence from 'ti.geofence';
const win = Ti.UI.createWindow();
const lbl = Ti.UI.createLabel({});
win.open();

win.addEventListener("click", function() {
	Ti.Geolocation.requestLocationPermissions(Ti.Geolocation.AUTHORIZATION_WHEN_IN_USE, function(e) {
		if (e.success) {
			ti_geofence.addGeofence({
				lon: 0,
				lat: 0,
				name: "zero",
				radius: 20
			})
			ti_geofence.addEventListener("transition", function(e){
				transition.text = e.name + " " + e.transition;
			})
			ti_geofence.startWatching();
		}
	});
})
