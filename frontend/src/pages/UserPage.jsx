import { logout } from "@/api/auth";
import { checkNicknameExists, getUser, updateUser } from "@/api/user";
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
	const [isNicknameValid, setIsNicknameValid] = useState(null); // null: 검사 전, true: 사용 가능, false: 중복

	//비밀번호 변경
	const [isChangingPassword, setIsChangingPassword] = useState(false);
	const [currentPassword, setCurrentPassword] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [confirmNewPassword, setConfirmNewPassword] = useState("");

	const [error, setError] = useState(null);

	useEffect(() => {
		const fetchUserInfo = async () => {
			setError(null);
			try {
				const { email, nickname, provider, createdAt } = await getUser();
				setEmail(email);
				setNickname(nickname);
				setNewNickname(nickname);
				setProvider(provider);
				setCreatedAt(createdAt.split("T")[0]);
			} catch (error) {
				setError(error);
			}
		};
		fetchUserInfo();
	}, [setEmail, setNickname, setProvider, setCreatedAt]);

	// nickname 입력창 변경 이벤트
	useEffect(() => {
		// nickname 중복 확인
		const checkNickname = async () => {
			if (
				newNickname.length < 2 ||
				newNickname.length > 10 ||
				newNickname === nickname
			) {
				setIsNicknameValid(null);
				return;
			}

			try {
				const { exists } = await checkNicknameExists(newNickname);
				setIsNicknameValid(!exists);
			} catch {
				setIsNicknameValid(null);
			}
		};

		const delay = setTimeout(checkNickname, 300);
		return () => clearTimeout(delay);
	}, [newNickname, nickname]);

	const handleUserupdate = async (e) => {
		e.preventDefault();
		setError("");
		if (!confirm("수정하시겠습니까?")) return;
		try {
			await updateUser({ nickname: newNickname });
			setNickname(newNickname);
			setIsEditing(false);
		} catch (error) {
			setError(error);
		}
	};
	const handlePasswordChange = () => {};

	const handleLogout = async () => {
		try {
			await logout();
		} finally {
			localStorage.removeItem("accessToken");
			navigate("/login");
		}
	};
	return (
		<div className="flex justify-center items-center min-h-screen bg-gray-100">
			<Card className="w-full max-w-sm min-w-sm m-6">
				<CardHeader className="text-center">
					<CardTitle className="text-2xl">
						{isChangingPassword ? "비밀번호 변경" : "내정보"}
					</CardTitle>
				</CardHeader>
				<CardContent>
					{!isChangingPassword && (
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
									value={currentPassword}
									onChange={(e) => setCurrentPassword(e.target.value)}
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
						</div>
					)}
				</CardContent>
				<CardFooter className="flex flex-col space-y-4">
					{isEditing && (
						<div className="w-full  space-y-4">
							<Button
								onClick={handleUserupdate}
								className="w-full cursor-pointer"
								disabled={isNicknameValid !== true}
							>
								수정
							</Button>
							<Button
								onClick={() => setIsEditing(false)}
								className="w-full cursor-pointer"
								variant="outline"
							>
								취소
							</Button>
						</div>
					)}
					{isChangingPassword && (
						<div className="w-full space-y-4">
							<Button onClick={handlePasswordChange} className="w-full cursor-pointer">
								비밀번호 변경
							</Button>
							<Button
								onClick={() => setIsChangingPassword(false)}
								className="w-full cursor-pointer"
								variant="outline"
							>
								취소
							</Button>
						</div>
					)}
					{!isEditing && !isChangingPassword && (
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
						</div>
					)}
				</CardFooter>
			</Card>
		</div>
	);
};
export default UserPage;
