import { logout } from "@/api/auth";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useNavigate, useLocation } from "react-router-dom";

const Header = () => {
	const navigate = useNavigate();
	const location = useLocation();

	const isLoggedIn = !!localStorage.getItem("accessToken");
	const isLoginPage = location.pathname === "/login";

	const handleLogout = async () => {
		try {
			await logout();
		} finally {
			localStorage.removeItem("accessToken");
			localStorage.removeItem("nickname");
			navigate("/login");
		}
	};

	return (
		<header>
			<div className="flex justify-between items-center m-6">
				<div className="font-bold">SkillUP</div>
				<div className="flex gap-2">
					{!isLoggedIn && !isLoginPage && (
						<Button
							variant="outline"
							className="cursor-pointer"
							onClick={() => navigate("/login")}
						>
							로그인
						</Button>
					)}
					{isLoggedIn && (
						<DropdownMenu>
							<DropdownMenuTrigger className="outline-none">
								<Avatar className="cursor-pointer size-12">
									<AvatarImage src="" />
									<AvatarFallback>ME</AvatarFallback>
								</Avatar>
							</DropdownMenuTrigger>
							<DropdownMenuContent>
								<DropdownMenuItem
									onClick={() => navigate("/me")}
									className="cursor-pointer"
								>
									내정보
								</DropdownMenuItem>
								<DropdownMenuItem onClick={handleLogout} className="cursor-pointer">
									로그아웃
								</DropdownMenuItem>
							</DropdownMenuContent>
						</DropdownMenu>
					)}
				</div>
			</div>
		</header>
	);
};

export default Header;
