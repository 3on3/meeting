// // 사용자의 현재 위치 정보를 가져오는 함수
// function getCurrentLocation() {
//   return new Promise((resolve, reject) => {
//     if (navigator.geolocation) {
//       navigator.geolocation.getCurrentPosition(resolve, reject);
//     } else {
//       reject(new Error('Geolocation is not supported by this browser.'));
//     }
//   });
// }
//
// // OpenWeatherMap API를 호출하여 날씨 데이터를 가져오는 함수
// async function fetchWeatherData(lat, lon) {
//   const apiKey = 'f17fb76a32d667c27e32c2c2ce62d6d7'; // OpenWeatherMap API 키
//   const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric`;
//
//   try {
//     const response = await fetch(url);
//     const data = await response.json();
//     return data;
//   } catch (error) {
//     console.error('Error fetching weather data:', error);
//     throw error;
//   }
// }
//
// // 날씨와 시간에 따라 배경 비디오를 설정하는 함수
// function setBackground(weatherData) {
//   const currentTime = new Date();
//   console.log(currentTime); // 현재 시간
//   const hours = currentTime.getHours();
//   const weatherCondition = weatherData.weather[0].main; // 날씨 상태
//   console.log(weatherCondition); // 날씨 상태
//   let videoSource = '';
//
//   // 날씨 상태와 시간에 따라 배경 비디오 지정
//   if (weatherCondition === 'Clear') {
//     if (hours >= 6 && hours < 14) {
//       // 맑은 아침
//       videoSource = '/assets/img/lalalala.mp4';
//     } else if (hours >= 14 && hours < 22) {
//       // 맑은 오후
//       videoSource = '/assets/img/pm2.mp4';
//     } else {
//       // 맑은 밤
//       videoSource = '/assets/img/night.mp4';
//     }
//   } else if (weatherCondition === 'Rain') {
//     if (hours >= 6 && hours < 14) {
//       // 비오는 아침
//       videoSource = '/assets/img/rainam.mp4';
//     } else if (hours >= 14 && hours < 22) {
//       // 비오는 오후
//       videoSource = '/assets/img/rainam.mp4';
//     } else {
//       // 비오는 밤
//       videoSource = '/assets/img/rainpm.mp4';
//     }
//   } else if (weatherCondition === 'Clouds') {
//     if (hours >= 6 && hours < 14) {
//       // 흐린 아침
//       videoSource = '/assets/img/cloud.mp4'; // 아직 없음
//     } else if (hours >= 14 && hours < 22) {
//       // 흐린 오후
//       videoSource = '/assets/img/cloud.mp4';
//     } else {
//       // 흐린 밤
//       videoSource = '/assets/img/night.mp4';
//     }
//   } else {
//     // 기본 배경
//     videoSource = '/assets/img/whiskey.mp4';
//   }
//
//   const videoElement = document.getElementById('backgroundVideo');
//   const sourceElement = document.getElementById('videoSource');
//   if (videoElement && sourceElement) {
//     sourceElement.src = videoSource;  // 비디오 소스를 설정
//     videoElement.load();  // 비디오를 다시 로드
//   } else {
//     console.error('Video element not found');
//   }
// }
//
// // 초기화 함수, 사용자 위치를 가져와 날씨 데이터를 설정
// async function init() {
//   try {
//     const position = await getCurrentLocation();
//     console.log(position); // 현재 유저 위치
//
//     const lat = position.coords.latitude;
//     const lon = position.coords.longitude;
//
//     const weatherData = await fetchWeatherData(lat, lon);
//
//     setBackground(weatherData);
//     setInterval(async () => {
//       const newWeatherData = await fetchWeatherData(lat, lon);
//       setBackground(newWeatherData);
//     }, 60000); // 1분마다 업데이트
//   } catch (error) {
//     console.error('Error getting location or weather data:', error);
//   }
// }
//
// // DOMContentLoaded 이벤트가 발생하면 init 함수 실행
// document.addEventListener('DOMContentLoaded', init);
// 배경 비디오 소스 목록
const videoSources = [
  '/assets/img/am.mp4',
  '/assets/img/pm2.mp4',
  '/assets/img/pm1.mp4',
  '/assets/img/rainpm.mp4',
  '/assets/img/cloud.mp4',
  '/assets/img/rainam.mp4',
  '/assets/img/dog.mp4'
];

let currentVideo = 1; // 현재 표시 중인 비디오

// 랜덤 배경 비디오 설정 함수
function setRandomBackground() {
  const randomIndex = Math.floor(Math.random() * videoSources.length);
  const videoSource = videoSources[randomIndex];

  const videoElement1 = document.getElementById('backgroundVideo1');
  const videoElement2 = document.getElementById('backgroundVideo2');
  const sourceElement1 = document.getElementById('videoSource1');
  const sourceElement2 = document.getElementById('videoSource2');

  if (currentVideo === 1) {
    sourceElement2.src = videoSource;  // 다음 비디오 소스 설정
    videoElement2.load();  // 다음 비디오 로드
    videoElement2.classList.remove('hidden'); // 다음 비디오 페이드 인
    videoElement1.classList.add('hidden'); // 현재 비디오 페이드 아웃
    currentVideo = 2; // 다음 비디오로 전환
  } else {
    sourceElement1.src = videoSource;  // 다음 비디오 소스 설정
    videoElement1.load();  // 다음 비디오 로드
    videoElement1.classList.remove('hidden'); // 다음 비디오 페이드 인
    videoElement2.classList.add('hidden'); // 현재 비디오 페이드 아웃
    currentVideo = 1; // 다음 비디오로 전환
  }
}

// 초기화 함수
function init() {
  setRandomBackground();
  setInterval(setRandomBackground, 10000); // 20초마다 배경 변경
}

// DOMContentLoaded 이벤트가 발생하면 init 함수 실행
document.addEventListener('DOMContentLoaded', init);




//////////////////////////////////////////////////////////// TEST 시연용 ///////////
// // 현시간에 맞춰 배경화면 변경 (임시로 3초설정 해놈)
// let counter = 0;

// function setBackgroundBasedOnCounter() {
// console.log("함수도 작동함");
// let backgroundUrl = '';

// if (counter % 3 === 0) {
//     backgroundUrl = 'url(/assets/img/Cinemagraph.gif)';
// } else if (counter % 3 === 1) {
//     backgroundUrl = 'url(/assets/img/rain.gif)';
// } else {
//     backgroundUrl = 'url(/assets/img/firepit.gif)';
// }

// document.querySelector('.main_banner').style.backgroundImage = backgroundUrl;
// counter++;
// console.log("배경바꼇을걸?");
// }

// function init() {
//     setBackgroundBasedOnCounter();
//     console.log("이벤트작동함");
//     // 10초마다 배경을 업데이트
//     setInterval(setBackgroundBasedOnCounter, 3000); // 10초마다 실행
// }

// document.addEventListener('DOMContentLoaded', init);

