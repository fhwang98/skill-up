import fetchWithAccess from "@/utils/fetchUtil";

const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

// 이메일 중복 검사
export const checkEmailExists = async (email) => {
	const res = await fetch(`${BASE_URL}/users/exist-email`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ email }),
	});
	if (!res.ok) {
		throw new Error("이메일 중복 검사 실패");
	}
	const { data } = await res.json();
	return data;
};

// 닉네임 중복 검사
export const checkNicknameExists = async (nickname) => {
	const res = await fetch(`${BASE_URL}/users/exist-nickname`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ nickname }),
	});
	if (!res.ok) {
		throw new Error("닉네임 중복 검사 실패");
	}
	const { data } = await res.json();
	return data;
};

// 회원가입
export const join = async ({ email, password, nickname }) => {
	const res = await fetch(`${BASE_URL}/users`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ email, password, nickname }),
	});

	if (!res.ok) {
		throw new Error("회원가입 실패");
	}

	const { data } = await res.json();
	return data;
};

// 유저 정보 조회
export const getUser = async () => {
	const res = await fetchWithAccess(`${BASE_URL}/users/me`, {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	});
	if (!res.ok) {
		throw new Error("유저 정보 조회 실패");
	}
	const { data } = await res.json();
	return data;
};

// 유저 정보 수정
export const updateUser = async ({ nickname }) => {
	const res = await fetchWithAccess(`${BASE_URL}/users/me`, {
		method: "PATCH",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ nickname }),
	});
	if (!res.ok) {
		throw new Error("유저 정보 수정 실패");
	}
	const { data } = await res.json();
	return data;
};
