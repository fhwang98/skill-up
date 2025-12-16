import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

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
		navigate("/");
	}, [navigate, query]);

	return <div>로그인 처리중...</div>;
};
export default LoginCallbackPage;
