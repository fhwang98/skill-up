const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const checkEmailExists = (email) =>
	fetch(`${BASE_URL}/users/exist-email`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ email }),
	});

export const checkNicknameExists = (nickname) =>
	fetch(`${BASE_URL}/users/exist-nickname`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ nickname }),
	});

export const join = ({ email, password, nickname }) =>
	fetch(`${BASE_URL}/users`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ email, password, nickname }),
	});
