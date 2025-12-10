import App from "@/App";
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
		],
	},
]);

export default router;
