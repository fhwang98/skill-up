import App from "@/App";
import JoinPage from "@/pages/JoinPage";
import MainPage from "@/pages/MainPage";
import { createBrowserRouter } from "react-router-dom";
import LoginPage from "@/pages/LoginPage";
import LoginCallbackPage from "@/pages/LoginCallbackPage";
import UserPage from "@/pages/UserPage";
import StudyCreatePage from "@/pages/StudyCreatePage";
import StudyLayout from "@/pages/StudyLayout";
import StudyUpdatePage from "@/pages/StudyUpdatePage";
import StudyPage from "@/pages/StudyPage";

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
				path: "join",
				element: <JoinPage />,
			},
			{
				path: "login",
				element: <LoginPage />,
			},
			{
				path: "oauth2/callback",
				element: <LoginCallbackPage />,
			},
			{
				path: "me",
				element: <UserPage />,
			},

			{
				path: "study/",
				element: <StudyLayout />,
				children: [
					{
						path: "add",
						element: <StudyCreatePage />,
					},
					{
						path: "edit/:id",
						element: <StudyUpdatePage />,
					},
					{
						path: ":id",
						element: <StudyPage />,
					},
				],
			},
		],
	},
]);

export default router;
