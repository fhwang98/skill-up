import { logout } from "@/api/auth";
import { getUser } from "@/api/user";
import { Button } from "@/components/ui/button";
import {
	Card,
	CardContent,
	CardFooter,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const UserPage = () => {
	const navigate = useNavigate();

	const [email, setEmail] = useState("");
	const [nickname, setNickname] = useState("");
	const [createdAt, setCreatedAt] = useState("");

	useEffect(() => {
		const fetchUserInfo = async () => {
			try {
				const { email, nickname, createdAt } = await getUser();
				setEmail(email);
				setNickname(nickname);
				setCreatedAt(createdAt.split("T")[0]);
			} catch (error) {
				console.error("Error fetching user info:", error);
			}
		};
		fetchUserInfo();
	}, [setEmail, setNickname, setCreatedAt]);

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
					<CardTitle className="text-2xl">내정보</CardTitle>
				</CardHeader>
				<CardContent>
					<div className="grid w-full items-center gap-4">
						<div className="flex justify-between">
							<Label className="w-20 text-gray-600">이메일</Label>
							<span>{email}</span>
						</div>
						<div className="flex justify-between">
							<Label className="w-20 text-gray-600">닉네임</Label>
							<span>{nickname}</span>
						</div>
						<div className="flex justify-between">
							<Label className="w-20 text-gray-600">가입일</Label>
							<span>{createdAt}</span>
						</div>
					</div>
				</CardContent>
				<CardFooter className="flex flex-col space-y-4">
					<Button
						variant="outline"
						onClick={handleLogout}
						className="w-full cursor-pointer"
					>
						로그아웃
					</Button>
				</CardFooter>
			</Card>
		</div>
	);
};
export default UserPage;
