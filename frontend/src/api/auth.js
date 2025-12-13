import fetchWithAccess from "@/utils/fetchUtil";

const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const login = (email, password) =>
	fetch(`${BASE_URL}/auth/login`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		credentials: "include",
		body: JSON.stringify({ email, password }),
	});

export const logout = () =>
	fetchWithAccess(`${BASE_URL}/auth/logout`, {
		method: "POST",
	});
