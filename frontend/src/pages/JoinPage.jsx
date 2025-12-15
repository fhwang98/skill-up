import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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
import { checkEmailExists, checkNicknameExists, createUser } from "@/api/user";

function JoinPage() {
	const navigate = useNavigate();

	// 회원가입 변수
	const [isEmailValid, setIsEmailValid] = useState(null); // null: 검사 전, true: 사용 가능, false: 중복
	const [isNicknameValid, setIsNicknameValid] = useState(null); // null: 검사 전, true: 사용 가능, false: 중복
	const [password, setPassword] = useState("");
	const [nickname, setNickname] = useState("");
	const [email, setEmail] = useState("");
	const [error, setError] = useState("");

	// email 입력창 변경 이벤트
	useEffect(() => {
		// email 중복 확인
		const checkEmail = async () => {
			if (
				email.length < 1 ||
				!email.includes("@") ||
				!email.includes(".") ||
				email.indexOf("@") > email.lastIndexOf(".")
			) {
				setIsEmailValid(null);
				return;
			}

			try {
				const response = await checkEmailExists(email);

				if (!response.success) {
					setIsEmailValid(null);
					return;
				}

				const { exists } = response.data;
				setIsEmailValid(!exists);
			} catch {
				setIsEmailValid(null);
			}
		};

		const delay = setTimeout(checkEmail, 300);
		return () => clearTimeout(delay);
	}, [email]);

	// nickname 입력창 변경 이벤트
	useEffect(() => {
		const checkNickname = async () => {
			if (nickname.length < 2 || nickname.length > 10) {
				setIsNicknameValid(null);
				return;
			}

			try {
				const response = await checkNicknameExists(nickname);

				if (!response.success) {
					setIsNicknameValid(null);
					return;
				}

				const { exists } = response.data;
				setIsNicknameValid(!exists);
			} catch {
				setIsNicknameValid(null);
			}
		};

		const delay = setTimeout(checkNickname, 300);
		return () => clearTimeout(delay);
	}, [nickname]);

	// 회원 가입 이벤트
	const handleSignUp = async (e) => {
		e.preventDefault();
		setError(null);

		if (password.length < 8 || nickname.trim() === "" || email.trim() === "") {
			setError("입력값을 다시 확인해주세요.");
			return;
		}

		try {
			const response = await createUser({ email, password, nickname });

			if (!response.success) {
				setError(response.error.message);
				return;
			}

			alert("회원가입이 완료되었습니다.");
			navigate("/login");
		} catch {
			setError("서버와 통신 중 오류가 발생했습니다.");
		}
	};

	// 페이지
	return (
		<div className="flex items-center justify-center min-h-screen bg-gray-100">
			<Card className="w-full max-w-sm min-w-sm m-6">
				<CardHeader className="text-center">
					<CardTitle className="text-2xl">회원 가입</CardTitle>
				</CardHeader>
				<form onSubmit={handleSignUp}>
					<CardContent>
						<div className="grid w-full items-center gap-4">
							<div className="flex flex-col space-y-1.5">
								<Label htmlFor="email">이메일</Label>
								<Input
									id="email"
									type="text"
									placeholder="이메일 주소"
									value={email}
									onChange={(e) => setEmail(e.target.value)}
									required
								/>
								{isEmailValid === null ? null : isEmailValid ? (
									<p className="text-sm text-green-600">사용 가능한 이메일입니다.</p>
								) : (
									<p className="text-sm text-red-600">이미 사용 중인 이메일입니다.</p>
								)}
							</div>
							<div className="flex flex-col space-y-1.5">
								<Label htmlFor="password">비밀번호</Label>
								<Input
									id="password"
									type="password"
									placeholder="비밀번호 (8자 이상)"
									value={password}
									onChange={(e) => setPassword(e.target.value)}
									required
									minLength={8}
								/>
							</div>
							<div className="flex flex-col space-y-1.5">
								<Label htmlFor="nickname">닉네임</Label>
								<Input
									id="nickname"
									type="text"
									placeholder="닉네임"
									value={nickname}
									onChange={(e) => setNickname(e.target.value)}
									required
								/>
								{isNicknameValid === null ? null : isNicknameValid ? (
									<p className="text-sm text-green-600">사용 가능한 닉네임입니다.</p>
								) : (
									<p className="text-sm text-red-600">이미 사용 중인 닉네임입니다.</p>
								)}
							</div>
							{error && <p className="text-sm text-red-600">{error}</p>}
						</div>
					</CardContent>
					<CardFooter className="flex flex-col space-y-4">
						<Button
							type="submit"
							className="w-full mt-6 cursor-pointer"
							disabled={
								isEmailValid !== true || isNicknameValid !== true || password.length < 8
							}
						>
							회원가입
						</Button>
					</CardFooter>
				</form>
			</Card>
		</div>
	);
}

export default JoinPage;
