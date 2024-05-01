
# Location Tracker App

The Location Tracker app allows users to track their location in real-time, save location data to a CSV file, and download the file for further analysis. It's designed for individuals who want to monitor their movement history or researchers who need to collect location data for studies or projects.

<div style="text-align: center;">
  <a href="https://drive.google.com/file/d/1jJ1S2ND9g4luKrhvmigXt-INwbt5I-yr/view?usp=sharing">Demo Video</a>
</div>

## Features

- Start and stop location tracking.
- Display current latitude and longitude coordinates.
- Save location data to a CSV file.
- Download the location data file for analysis.

## Tech Stack

The Location Tracker app is built using the following technologies:

- **Java**: The primary programming language for building the Device location tracker.
- **Android SDK**: Android Software Development Kit.
- **Android Location API**: Allows the app to access location information from the device's GPS and network providers.
- **LiveData**: Android Architecture Component for observing and reacting to data changes.
- **Service**: Android component for background processing, used for continuous location tracking.
- **FileProvider**: Android support library for accessing files securely.
- **CSV File Handling**: Writing location data to CSV files for storage and analysis.

## Usage

1. Start the app and press the "Let's Explore" button to begin location tracking.
2. On Click on "Start fetch" it will ask for location permission on it's granted it will start location update service to fetch location in every 2 seconds.
3. The app will display your current latitude and longitude coordinates in real-time.
4. Press the "Stop" button it will stop & unbind register location tracking service.
5. After stopping tracking, press the "Download" button to save the location data to a CSV file.
6. You can then access the downloaded file for further analysis or sharing.

## Privacy and Data Security

- Location data is stored locally on the user's device and is not shared with any third parties.
- Users have full control over their data and can delete it at any time.

## Future Enhancements

- Integration with cloud storage for automatic backup of location data.
- Visualization features to display tracked routes on a map within the app.
- Customizable settings for tracking intervals and data storage options.

---
