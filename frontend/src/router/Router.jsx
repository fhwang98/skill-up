import App from "@/App";
import JoinPage from "@/pages/JoinPage";
import MainPage from "@/pages/MainPage";
import { createBrowserRouter } from "react-router-dom";
import LoginPage from "@/pages/LoginPage";
import LoginCallbackPage from "@/pages/LoginCallbackPage";

const router = createBrowserRouter([
	{
		path: "/",
		element: <App />,
		children: [
			{
				path: "",
				element: <MainPage />,
			},
			{
				path: "/join",
				element: <JoinPage />,
			},
			{
				path: "/login",
				element: <LoginPage />,
			},
			{
				path: "/oauth2/callback",
				element: <LoginCallbackPage />,
			},
		],
	},
]);

export default router;
