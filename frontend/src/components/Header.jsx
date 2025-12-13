import { logout } from "@/api/auth";
import { Button } from "@/components/ui/button";
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
			navigate("/login");
		}
	};

	return (
		<header>
			<div className="flex justify-between items-center p-4">
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
						<Button
							variant="outline"
							onClick={handleLogout}
							className="cursor-pointer"
						>
							로그아웃
						</Button>
					)}
				</div>
			</div>
		</header>
	);
};

export default Header;
