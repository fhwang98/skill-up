import { logout } from "@/api/auth";
import {
	changePassword,
	checkNicknameExists,
	deleteUser,
	getUser,
	updateUser,
} from "@/api/user";
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
import { Separator } from "@/components/ui/separator";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const UserPage = () => {
	const navigate = useNavigate();

	//정보 조회
	const [email, setEmail] = useState("");
	const [nickname, setNickname] = useState("");
	const [provider, setProvider] = useState("");
	const [createdAt, setCreatedAt] = useState("");

	//정보 수정
	const [isEditing, setIsEditing] = useState(false);
	const [newNickname, setNewNickname] = useState("");
	const [isNicknameValid, setIsNicknameValid] = useState(null);

	//비밀번호 변경
	const [isChangingPassword, setIsChangingPassword] = useState(false);
	const [password, setPassword] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [confirmNewPassword, setConfirmNewPassword] = useState("");

	const [isDeleting, setIsDeleting] = useState(false);

	const [error, setError] = useState(null);

	// 화면 진입시 초기화
	useEffect(() => {
		setPassword("");
		setNewPassword("");
		setConfirmNewPassword("");
		setError(null);
	}, [isEditing, isChangingPassword, isDeleting]);

	// 유저 정보 조회
	useEffect(() => {
		const fetchUserInfo = async () => {
			setError(null);
			try {
				const response = await getUser();
				if (!response.success) {
					setError(response.error.message);
					return;
				}
				const { email, nickname, provider, createdAt } = response.data;
				setEmail(email);
				setNickname(nickname);
				setNewNickname(nickname);
				setProvider(provider);
				setCreatedAt(createdAt.split("T")[0]);
			} catch (err) {
				setError(err?.message ?? "요청 중 오류가 발생했습니다.");
			}
		};
		fetchUserInfo();
	}, []);

	// nickname 입력창 변경 이벤트
	useEffect(() => {
		const checkNickname = async () => {
			if (newNickname === nickname) {
				setIsNicknameValid(null);
				return;
			}

			if (newNickname.length < 2 || newNickname.length > 10) {
				setIsNicknameValid(null);
				return;
			}

			try {
				const response = await checkNicknameExists(newNickname);
				if (!response.success) {
					setIsNicknameValid(null);
					return;
				}
				const { exists } = response.data;
				setIsNicknameValid(!exists);
			} catch (err) {
				setIsNicknameValid(null);
			}
		};

		const delay = setTimeout(checkNickname, 300);
		return () => clearTimeout(delay);
	}, [newNickname, nickname]);

	// 정보 수정 이벤트
	const handleUserUpdate = async (e) => {
		e.preventDefault();
		setError(null);
		if (!confirm("수정하시겠습니까?")) return;
		try {
			const response = await updateUser({ nickname: newNickname });
			if (!response.success) {
				setError(response.error.message);
				return;
			}
			setNickname(newNickname);
			setIsEditing(false);
		} catch (err) {
			setError(err?.message ?? "요청 중 오류가 발생했습니다.");
		}
	};

	// 비밀번호 변경 이벤트
	const handlePasswordChange = async (e) => {
		e.preventDefault();
		setError(null);

		if (password === "") {
			setError("비밀번호를 입력하세요.");
			return;
		}
		if (newPassword.length < 8) {
			setError("새 비밀번호는 최소 8자 이상이어야 합니다.");
			return;
		}
		if (newPassword !== confirmNewPassword) {
			setError("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			return;
		}

		if (!confirm("비밀번호를 변경하시겠습니까?")) return;
		try {
			const response = await changePassword({ password, newPassword });
			if (!response.success) {
				setError(response.error.message);
				return;
			}
			handleLogout();
		} catch (err) {
			setError(err?.message ?? "요청 중 오류가 발생했습니다.");
		}
	};

	// 로그아웃 이벤트
	const handleLogout = async () => {
		try {
			await logout();
		} finally {
			localStorage.removeItem("accessToken");
			navigate("/login");
		}
	};

	// 회원 탈퇴 이벤트
	const handleWithdrawal = async (e) => {
		e.preventDefault();
		setError(null);
		if (provider === "LOCAL" && password === "") {
			setError("비밀번호를 입력하세요.");
			return;
		}
		if (!confirm("탈퇴 후 되돌릴 수 없습니다. 정말 탈퇴하시겠습니까?")) return;

		try {
			const response = await deleteUser({ password });
			if (!response.success) {
				setError(response.error.message);
				return;
			}
			alert("회원 탈퇴가 완료되었습니다.");
			handleLogout();
		} catch (err) {
			setError(err?.message ?? "요청 중 오류가 발생했습니다.");
		}
	};

	return (
		<div className="flex justify-center items-center min-h-screen bg-gray-100">
			<Card className="w-full max-w-sm min-w-sm m-6">
				<CardHeader className="text-center">
					<CardTitle className="text-2xl">
						{isChangingPassword
							? "비밀번호 변경"
							: isDeleting
							? "회원 탈퇴"
							: "내 정보"}
					</CardTitle>
				</CardHeader>
				<CardContent>
					{!isChangingPassword && !isDeleting && (
						<div>
							<div className="grid w-full items-center gap-4">
								<div className="flex justify-between">
									<Label className="w-1/3">이메일</Label>
									<span>{email}</span>
								</div>

								{isEditing ? (
									<div className="flex flex-col">
										<div className="flex justify-between">
											<Label className="w-1/3">닉네임</Label>
											<Input
												value={newNickname}
												onChange={(e) => setNewNickname(e.target.value)}
												className="max-w-40"
											/>
										</div>

										<div className="flex justify-end">
											{isNicknameValid === null ? null : isNicknameValid ? (
												<p className="text-sm text-green-600">사용 가능한 닉네임입니다.</p>
											) : (
												<p className="text-sm text-red-600">이미 사용 중인 닉네임입니다.</p>
											)}
										</div>
									</div>
								) : (
									<div className="flex justify-between">
										<Label className="w-1/3">닉네임</Label>
										<span>{nickname}</span>
									</div>
								)}
								<div className="flex justify-between">
									<Label className="w-1/3">가입일</Label>
									<span>{createdAt}</span>
								</div>
								<div className="flex justify-between">
									<Label className="w-1/3">소셜</Label>
									<span>
										{provider === "KAKAO" && "카카오"}
										{provider === "NAVER" && "네이버"}
									</span>
								</div>

								{error && <p className="text-sm text-red-600">{error}</p>}
							</div>
						</div>
					)}
					{isChangingPassword && (
						<div className=" space-y-4">
							<div className="flex justify-between items-center gap-4">
								<Label className="w-1/3">현재 비밀번호</Label>
								<Input
									type="password"
									value={password}
									onChange={(e) => setPassword(e.target.value)}
								/>
							</div>

							<Separator />
							<div className="flex justify-between items-center gap-4">
								<Label className="w-1/3">새 비밀번호</Label>
								<Input
									type="password"
									value={newPassword}
									onChange={(e) => setNewPassword(e.target.value)}
								/>
							</div>

							<div className="flex justify-between items-center gap-4">
								<Label className="w-1/3">비밀번호 확인</Label>
								<Input
									type="password"
									value={confirmNewPassword}
									onChange={(e) => setConfirmNewPassword(e.target.value)}
								/>
							</div>
							{error ? (
								<p className="text-sm text-red-600 text-right">{error}</p>
							) : newPassword.length > 0 &&
							  (newPassword.length < 8 || newPassword.length > 16) ? (
								<p className="text-sm text-red-600 text-right">
									비밀번호는 8~16자 이내로 입력해주세요.
								</p>
							) : confirmNewPassword.length > 0 &&
							  newPassword !== confirmNewPassword ? (
								<p className="text-sm text-red-600 text-right">
									새 비밀번호와 비밀번호 확인이 일치하지 않습니다.
								</p>
							) : null}
						</div>
					)}
					{isDeleting && (
						<div className=" space-y-4">
							{provider === "LOCAL" && (
								<div className="flex justify-between items-center gap-4">
									<Label className="w-1/3">비밀번호</Label>
									<Input
										type="password"
										value={password}
										onChange={(e) => setPassword(e.target.value)}
									/>
								</div>
							)}

							{error && <p className="text-sm text-red-600 text-right">{error}</p>}
						</div>
					)}
				</CardContent>
				<CardFooter className="flex flex-col space-y-4">
					{isEditing && (
						<div className="w-full  space-y-4">
							<Button
								onClick={handleUserUpdate}
								className="w-full cursor-pointer"
								disabled={isNicknameValid !== true || newNickname === nickname}
							>
								수정
							</Button>
							<Button
								onClick={() => {
									setError(null);
									setIsEditing(false);
									setNewNickname(nickname);
								}}
								className="w-full cursor-pointer"
								variant="outline"
							>
								취소
							</Button>
						</div>
					)}
					{isChangingPassword && (
						<div className="w-full space-y-4">
							<Button
								onClick={handlePasswordChange}
								className="w-full cursor-pointer"
								disabled={newPassword !== confirmNewPassword || newPassword.length < 8}
							>
								비밀번호 변경
							</Button>
							<Button
								onClick={() => {
									setError(null);
									setPassword("");
									setNewPassword("");
									setConfirmNewPassword("");
									setIsChangingPassword(false);
								}}
								className="w-full cursor-pointer"
								variant="outline"
							>
								취소
							</Button>
						</div>
					)}
					{isDeleting && (
						<div className="w-full space-y-4">
							<Button
								onClick={handleWithdrawal}
								variant="destructive"
								className="w-full cursor-pointer"
								disabled={provider === "LOCAL" && password === ""}
							>
								탈퇴하기
							</Button>
							<Button
								onClick={() => {
									setError(null);
									setPassword("");
									setIsDeleting(false);
								}}
								variant="outline"
								className="w-full cursor-pointer"
							>
								취소
							</Button>
						</div>
					)}
					{!isEditing && !isChangingPassword && !isDeleting && (
						<div className="w-full space-y-4">
							{provider === "LOCAL" && (
								<div className="w-full space-y-4">
									<Button
										variant="outline"
										onClick={() => setIsEditing(true)}
										className="w-full cursor-pointer"
									>
										회원 정보 수정
									</Button>
									<Button
										variant="outline"
										onClick={() => setIsChangingPassword(true)}
										className="w-full cursor-pointer"
									>
										비밀번호 변경
									</Button>
								</div>
							)}
							<Button onClick={handleLogout} className="w-full cursor-pointer">
								로그아웃
							</Button>
							<Button
								variant="destructive"
								onClick={() => setIsDeleting(true)}
								className="w-full cursor-pointer"
							>
								회원 탈퇴
							</Button>
						</div>
					)}
				</CardFooter>
			</Card>
		</div>
	);
};
export default UserPage;
