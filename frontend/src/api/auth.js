const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const login = async (email, password) => {
	const res = await fetch(`${BASE_URL}/auth/login`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		credentials: "include",
		body: JSON.stringify({ email, password }),
	});

	if (!res.ok) {
		throw new Error("로그인 실패");
	}

	const { data } = await res.json();
	return data;
};
export const logout = () =>
	fetch(`${BASE_URL}/auth/logout`, {
		method: "POST",
		credentials: "include",
		headers: {
			Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
		},
	});
