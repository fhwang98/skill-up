const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const login = async (email, password) => {
	const res = await fetch(`${BASE_URL}/auth/login`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		credentials: "include",
		body: JSON.stringify({ email, password }),
	});

	if (!res) {
		throw new Error("서버와 통신할 수 없습니다.");
	}

	return await res.json();
};
export const logout = () =>
	fetch(`${BASE_URL}/auth/logout`, {
		method: "POST",
		credentials: "include",
		headers: {
			Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
		},
	});
