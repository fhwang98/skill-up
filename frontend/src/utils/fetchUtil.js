const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

// AccessToken 만료시 Refreshing
export async function refreshAccessToken() {
	const res = await fetch(`${BASE_URL}/auth/refresh`, {
		method: "POST",
		credentials: "include",
	});

	if (!res.ok) throw new Error("RefreshToken 만료");

	// 성공시 새 Token 저장
	const response = await res.json();
	const data = response.data;
	localStorage.setItem("accessToken", data.accessToken);

	return data.accessToken;
}

// AccessToken과 함께 fetch
export async function fetchWithAccess(url, options = {}) {
	// 로컬 스토리지로 부터 AccessToken 가져옴
	let accessToken = localStorage.getItem("accessToken");
	if (!accessToken) {
		throw new Error("인증이 필요합니다.");
	}

	// 옵션에 Header 없는 경우 추가 + AccessToken 부착
	if (!options.headers) options.headers = {};
	options.headers["Authorization"] = `Bearer ${accessToken}`;
	// credentials 포함
	if (!options.credentials) {
		options.credentials = "include";
	}

	// 요청 진행
	let res = await fetch(url, options);

	// AccessToken 만료로 401 뜨면, Refresh로 재발급
	if (res.status === 401) {
		try {
			accessToken = await refreshAccessToken();
			options.headers["Authorization"] = `Bearer ${accessToken}`;
			// 재요청
			res = await fetch(url, options);
		} catch (err) {
			// refresh 실패 → 강제 로그아웃
			localStorage.removeItem("accessToken");
			window.location.href = "/login";
			throw err;
		}
	}

	return res;
}
export default fetchWithAccess;
