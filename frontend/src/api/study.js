import fetchWithAccess from "@/utils/fetchUtil";

const BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const createStudy = async ({
	title,
	description,
	maxMembers,
	recruitEndDate,
	startDate,
	category,
	endDate,
	tags,
}) => {
	const res = await fetchWithAccess(`${BASE_URL}/studies`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({
			title,
			description,
			maxMembers,
			recruitEndDate,
			startDate,
			category,
			endDate,
			tags,
		}),
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};

export const getStudies = async ({ page, keyword, category, status }) => {
	const params = new URLSearchParams({ page });
	if (keyword) params.append("keyword", keyword);
	if (category) params.append("category", category);
	if (status) params.append("status", status);

	const res = await fetch(`${BASE_URL}/studies?${params.toString()}`, {
		method: "GET",
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};

export const getStudyDetail = async (id) => {
	const res = await fetch(`${BASE_URL}/studies/${id}`, {
		method: "GET",
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};

export const updateStudy = async (id, payload) => {
	const res = await fetchWithAccess(`${BASE_URL}/studies/${id}`, {
		method: "PATCH",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(payload),
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};

export const deleteStudy = async (id) => {
	const res = await fetchWithAccess(`${BASE_URL}/studies/${id}`, {
		method: "DELETE",
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};

export const getTags = async (keyword) => {
	const params = new URLSearchParams({ keyword });
	const res = await fetch(`${BASE_URL}/tags?${params.toString()}`, {
		method: "GET",
	});

	if (!res) throw new Error("서버와 통신할 수 없습니다.");
	return await res.json();
};
