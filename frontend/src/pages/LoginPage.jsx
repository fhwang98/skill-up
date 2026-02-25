import { Button } from "@/components/ui/button";
import {
	Card,
	CardContent,
	CardFooter,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import kakaoLoginUrl from "@/assets/images/kakao_login_medium_narrow.png";
import naverLoginUrl from "@/assets/images/NAVER_login_Light_KR_green_narrow_H56.png";
import { login } from "@/api/auth";
import { getUser } from "@/api/user";

// .env로 부터 백엔드 URL 받아오기
const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

const LoginPage = () => {
	// 자체 로그인시 username/password 변수
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");

	const navigate = useNavigate();

	const [searchParams] = useSearchParams();
	const errorCode = searchParams.get("error");

	useEffect(() => {
		if (!errorCode) return;

		switch (errorCode) {
			case "DUPLICATE_EMAIL":
				alert("이미 사용중인 이메일입니다.");
				break;
			case "INVALID_PROVIDER":
				alert("지원하지 않는 로그인 방식입니다.");
				break;
			default:
				alert("소셜 로그인에 실패했습니다.");
		}

		navigate("/login", { replace: true });
	}, [errorCode, navigate]);

	// 자체 로그인 이벤트
	const handleLogin = async (e) => {
		e.preventDefault();
		setError(null);

		if (email === "" || password === "") {
			setError("이메일과 비밀번호를 입력하세요.");
			return;
		}

		try {
			const response = await login(email, password);

			if (!response.success) {
				setError(response.error.message);
				return;
			}

			const { accessToken } = response.data;
			localStorage.setItem("accessToken", accessToken);

			// 닉네임 저장 (리더 판별용)
			try {
				const userResponse = await getUser();
				if (userResponse.success) {
					localStorage.setItem("nickname", userResponse.data.nickname);
				}
			} catch {
				// nickname 저장 실패해도 로그인은 정상 처리
			}

			navigate("/");
		} catch {
			setError("서버와 통신할 수 없습니다.");
		}
	};

	// 소셜 로그인 이벤트
	const handleSocialLogin = async (provider) => {
		window.location.href = `${BACKEND_API_BASE_URL}/oauth2/authorization/${provider}`;
	};

	return (
		<div className="flex justify-center items-center min-h-screen bg-gray-100">
			<Card className="w-full max-w-sm min-w-sm m-6">
				<CardHeader className="text-center">
					<CardTitle className="text-2xl">로그인</CardTitle>
				</CardHeader>
				<CardContent>
					<form onSubmit={handleLogin}>
						<div className="grid w-full items-center gap-4">
							<div className="flex flex-col space-y-1.5">
								<Label htmlFor="email">이메일</Label>
								<Input
									id="email"
									type="text"
									placeholder="이메일"
									value={email}
									onChange={(e) => setEmail(e.target.value)}
									required
								/>
							</div>
							<div className="flex flex-col space-y-1.5">
								<Label htmlFor="password">비밀번호</Label>
								<Input
									id="password"
									type="password"
									placeholder="비밀번호"
									value={password}
									onChange={(e) => setPassword(e.target.value)}
									required
								/>
							</div>
							{error && <p className="text-sm text-red-500">{error}</p>}
							<Button type="submit" className="w-full cursor-pointer">
								로그인
							</Button>
							<Button
								variant="outline"
								className="text-sm w-full cursor-pointer"
								onClick={() => navigate("/join")}
							>
								회원가입
							</Button>
						</div>
					</form>
				</CardContent>
				<CardFooter className="flex flex-col space-y-4">
					<div className="relative w-full">
						<div className="absolute inset-0 flex items-center">
							<span className="w-full border-t" />
						</div>
						<div className="relative flex justify-center text-xs uppercase">
							<span className="bg-background px-2 text-muted-foreground">
								Or continue with
							</span>
						</div>
					</div>
					<div className="grid grid-cols-2 gap-4 w-full">
						<img
							src={kakaoLoginUrl}
							alt="KAKAO 로그인 버튼"
							onClick={() => handleSocialLogin("kakao")}
							className="cursor-pointer"
						/>
						<img
							src={naverLoginUrl}
							alt="NAVER 로그인 버튼"
							onClick={() => handleSocialLogin("naver")}
							className="cursor-pointer"
						/>
					</div>
				</CardFooter>
			</Card>
		</div>
	);
};
export default LoginPage;
