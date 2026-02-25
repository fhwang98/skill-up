import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getStudyDetail, deleteStudy } from "@/api/study";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { CalendarDays, Users, Eye, Tag } from "lucide-react";

const CATEGORY_LABEL = {
	BACKEND: "백엔드",
	FRONTEND: "프론트엔드",
	ALGORITHM: "알고리즘",
	CS: "CS",
	PROJECT: "프로젝트",
};

const StudyPage = () => {
	const { id } = useParams();
	const navigate = useNavigate();

	const [study, setStudy] = useState(null);
	const [loading, setLoading] = useState(true);
	const [isLeader, setIsLeader] = useState(false);

	useEffect(() => {
		const fetchDetail = async () => {
			try {
				const response = await getStudyDetail(id);
				if (!response.success) {
					alert("존재하지 않는 스터디입니다.");
					navigate("/");
					return;
				}
				setStudy(response.data);

				// 로그인 유저의 닉네임과 리더 닉네임 비교
				const myNickname = localStorage.getItem("nickname");
				if (myNickname && myNickname === response.data.leaderNickname) {
					setIsLeader(true);
				}
			} catch {
				alert("스터디 정보를 불러오지 못했습니다.");
				navigate("/");
			} finally {
				setLoading(false);
			}
		};

		fetchDetail();
	}, [id, navigate]);

	const handleDelete = async () => {
		if (!confirm("스터디를 삭제하시겠습니까?")) return;
		try {
			const response = await deleteStudy(id);
			if (!response.success) {
				alert(response.error?.message || "삭제에 실패했습니다.");
				return;
			}
			alert("스터디가 삭제되었습니다.");
			navigate("/");
		} catch {
			alert("삭제 중 오류가 발생했습니다.");
		}
	};

	if (loading) {
		return (
			<div className="min-h-screen flex items-center justify-center text-gray-400">
				불러오는 중...
			</div>
		);
	}

	if (!study) return null;

	return (
		<div className="min-h-screen py-12">
			<div className="max-w-3xl mx-auto px-4">
				{/* 뒤로가기 */}
				<button
					onClick={() => navigate(-1)}
					className="mb-6 text-sm text-gray-400 hover:text-gray-600 flex items-center gap-1 cursor-pointer"
				>
					← 목록으로
				</button>

				{/* 헤더 */}
				<div className="bg-white rounded-xl border p-6 mb-4">
					<div className="flex items-start justify-between gap-4">
						<div className="flex-1">
							<div className="flex gap-2 mb-3">
								{study.status === "OPENED" ? (
									<Badge className="bg-green-600">모집중</Badge>
								) : (
									<Badge variant="secondary">모집종료</Badge>
								)}
								<Badge variant="outline">
									{CATEGORY_LABEL[study.category] ?? study.category}
								</Badge>
							</div>
							<h1 className="text-2xl font-bold text-gray-900 mb-2">
								{study.title}
							</h1>
							<p className="text-sm text-gray-500">
								{study.leaderNickname} · {study.createdAt?.slice(0, 10)}
							</p>
						</div>

						{/* 리더 전용 버튼 */}
						{isLeader && (
							<div className="flex gap-2 shrink-0">
								<Button
									variant="outline"
									size="sm"
									className="cursor-pointer"
									onClick={() => navigate(`/study/edit/${id}`)}
								>
									수정
								</Button>
								<Button
									variant="destructive"
									size="sm"
									className="cursor-pointer"
									onClick={handleDelete}
								>
									삭제
								</Button>
							</div>
						)}
					</div>
				</div>

				{/* 스터디 정보 */}
				<div className="bg-white rounded-xl border p-6 mb-4">
					<h2 className="text-sm font-semibold text-gray-500 mb-4">스터디 정보</h2>
					<div className="grid grid-cols-2 gap-4 text-sm">
						<div className="flex items-center gap-2 text-gray-600">
							<Users size={15} />
							<span>인원 {study.currentMembers} / {study.maxMembers}명</span>
						</div>
						<div className="flex items-center gap-2 text-gray-600">
							<Eye size={15} />
							<span>조회수 {study.viewCount}</span>
						</div>
						<div className="flex items-center gap-2 text-gray-600">
							<CalendarDays size={15} />
							<span>모집 마감 {study.recruitEndDate}</span>
						</div>
						<div className="flex items-center gap-2 text-gray-600">
							<CalendarDays size={15} />
							<span>
								스터디 기간 {study.startDate}
								{study.endDate ? ` ~ ${study.endDate}` : " ~"}
							</span>
						</div>
					</div>

					{study.tags?.length > 0 && (
						<>
							<Separator className="my-4" />
							<div className="flex items-center gap-2 flex-wrap">
								<Tag size={14} className="text-gray-400" />
								{study.tags.map((tag) => (
									<Badge key={tag} variant="secondary">{tag}</Badge>
								))}
							</div>
						</>
					)}
				</div>

				{/* 상세 설명 */}
				<div className="bg-white rounded-xl border p-6">
					<h2 className="text-sm font-semibold text-gray-500 mb-4">스터디 소개</h2>
					<p className="text-sm text-gray-700 whitespace-pre-wrap leading-relaxed">
						{study.description}
					</p>
				</div>
			</div>
		</div>
	);
};

export default StudyPage;
