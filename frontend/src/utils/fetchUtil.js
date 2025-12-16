const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

// AccessToken 만료시 Refreshing
export async function refreshAccessToken() {
	const res = await fetch(`${BASE_URL}/auth/refresh`, {
		method: "POST",
		credentials: "include",
	});

	if (!res.ok) throw new Error("서버와 통신할 수 없습니다.");

	// 성공시 새 Token 저장
	const response = await res.json();
	const { accessToken } = response.data;
	localStorage.setItem("accessToken", accessToken);

	return accessToken;
}

// AccessToken과 함께 fetch
export async function fetchWithAccess(url, options = {}) {
	// 로컬 스토리지로 부터 AccessToken 가져옴
	let accessToken = localStorage.getItem("accessToken");
	if (!accessToken) throw new Error("인증이 필요합니다.");

	// 옵션에 Header 없는 경우 추가 + AccessToken 부착
	options.headers = {
		...(options.headers || {}),
		Authorization: `Bearer ${accessToken}`,
	};
	options.credentials = options.credentials || "include";

	let res = await fetch(url, options);

	if (res.status === 401) {
		accessToken = await refreshAccessToken();
		options.headers.Authorization = `Bearer ${accessToken}`;
		res = await fetch(url, options);
	}

	return res;
}
export default fetchWithAccess;
