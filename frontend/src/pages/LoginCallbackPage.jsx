import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { getUser } from "@/api/user";

const LoginCallbackPage = () => {
	const navigate = useNavigate();
	const [query, setQuery] = useSearchParams();

	useEffect(() => {
		const accessToken = query.get("accessToken");

		if (!accessToken) {
			alert("소셜 로그인 실패");
			navigate("/login");
			return;
		}

		localStorage.setItem("accessToken", accessToken);

		// 닉네임 저장 (리더 판별용)
		getUser()
			.then((res) => {
				if (res.success) localStorage.setItem("nickname", res.data.nickname);
			})
			.catch(() => {})
			.finally(() => navigate("/"));
	}, [navigate, query]);

	return <div>로그인 처리중...</div>;
};
export default LoginCallbackPage;
