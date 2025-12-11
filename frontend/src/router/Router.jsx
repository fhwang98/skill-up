import App from "@/App";
import JoinPage from "@/pages/JoinPage";
import MainPage from "@/pages/MainPage";
import { createBrowserRouter } from "react-router-dom";

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
		],
	},
]);

export default router;
