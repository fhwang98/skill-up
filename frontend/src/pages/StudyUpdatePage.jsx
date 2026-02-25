import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { getStudyDetail, updateStudy, getTags } from "@/api/study";

const STATUS_OPTIONS = [
	{ value: "OPENED", label: "모집중" },
	{ value: "CLOSED", label: "모집종료" },
];

const StudyUpdatePage = () => {
	const { id } = useParams();
	const navigate = useNavigate();

	const [form, setForm] = useState(null); // null = 로딩 중
	const [tagKeyword, setTagKeyword] = useState("");
	const [tagResults, setTagResults] = useState([]);
	const [selectedIndex, setSelectedIndex] = useState(0);
	const [error, setError] = useState("");

	const today = new Date().toISOString().split("T")[0];

	// 기존 스터디 데이터 불러오기
	useEffect(() => {
		const token = localStorage.getItem("accessToken");
		if (!token) {
			alert("로그인 후 이용할 수 있습니다.");
			navigate("/login", { replace: true });
			return;
		}

		const fetchStudy = async () => {
			try {
				const response = await getStudyDetail(id);
				if (!response.success) {
					alert("스터디를 불러올 수 없습니다.");
					navigate("/");
					return;
				}

				const data = response.data;

				// 리더 본인만 접근 가능
				const myNickname = localStorage.getItem("nickname");
				if (!myNickname || myNickname !== data.leaderNickname) {
					alert("수정 권한이 없습니다.");
					navigate(`/study/${id}`);
					return;
				}

				setForm({
					title: data.title,
					description: data.description,
					maxMembers: data.maxMembers,
					category: data.category,
					status: data.status,
					tags: data.tags ?? [],
					startDate: data.startDate ?? "",
					endDate: data.endDate ?? "",
					recruitEndDate: data.recruitEndDate ?? "",
				});
			} catch {
				alert("스터디 정보를 불러오지 못했습니다.");
				navigate("/");
			}
		};

		fetchStudy();
	}, [id, navigate]);

	// 태그 자동완성
	useEffect(() => {
		const searchTags = async () => {
			if (!tagKeyword) {
				setTagResults([]);
				return;
			}
			try {
				const response = await getTags(tagKeyword);
				if (!response.success) { setTagResults([]); return; }
				setTagResults(response.data);
			} catch {
				setTagResults([]);
			}
		};
		const delay = setTimeout(searchTags, 300);
		return () => clearTimeout(delay);
	}, [tagKeyword]);

	const handleChange = (key, value) => {
		setForm((prev) => ({ ...prev, [key]: value }));
	};

	const filteredTagResults = tagResults.filter(
		(tag) => !form?.tags.includes(tag)
	);

	const addTag = (tag) => {
		if (form.tags.includes(tag)) return;
		setForm((prev) => ({ ...prev, tags: [...prev.tags, tag] }));
		setTagKeyword("");
		setTagResults([]);
	};

	const removeTag = (tag) => {
		setForm((prev) => ({ ...prev, tags: prev.tags.filter((t) => t !== tag) }));
	};

	const handleKeyDown = (e) => {
		if (filteredTagResults.length === 0) return;
		switch (e.key) {
			case "ArrowDown":
				e.preventDefault();
				setSelectedIndex((prev) => Math.min(prev + 1, filteredTagResults.length - 1));
				break;
			case "ArrowUp":
				e.preventDefault();
				setSelectedIndex((prev) => Math.max(prev - 1, 0));
				break;
			case "Enter":
				e.preventDefault();
				if (filteredTagResults[selectedIndex]) addTag(filteredTagResults[selectedIndex]);
				break;
			default:
				break;
		}
	};

	const isSubmitDisabled =
		!form?.title ||
		!form?.description ||
		!form?.maxMembers ||
		!form?.category ||
		!form?.status ||
		!form?.startDate ||
		!form?.recruitEndDate;

	const handleSubmit = async (e) => {
		e.preventDefault();
		setError("");
		try {
			const response = await updateStudy(id, {
				title: form.title,
				description: form.description,
				maxMembers: Number(form.maxMembers),
				category: form.category,
				status: form.status,
				tags: form.tags,
				startDate: form.startDate,
				recruitEndDate: form.recruitEndDate,
				endDate: form.endDate || null,
			});

			if (!response.success) {
				setError(response.error?.message || "수정에 실패했습니다.");
				return;
			}

			alert("스터디가 수정되었습니다.");
			navigate(`/study/${id}`);
		} catch {
			alert("수정 중 오류가 발생했습니다.");
		}
	};

	if (!form) {
		return (
			<div className="min-h-screen flex items-center justify-center text-gray-400">
				불러오는 중...
			</div>
		);
	}

	return (
		<div className="min-h-screen mx-12 py-12">
			<div className="max-w-3xl mx-auto px-4">
				<h1 className="text-2xl font-bold mb-8">스터디 수정</h1>

				<form className="space-y-6" onSubmit={handleSubmit}>
					{/* 제목 */}
					<div className="space-y-2">
						<Label>스터디 제목</Label>
						<Input
							value={form.title}
							onChange={(e) => handleChange("title", e.target.value)}
						/>
					</div>

					{/* 설명 */}
					<div className="space-y-2">
						<Label>스터디 설명</Label>
						<Textarea
							className="min-h-50"
							value={form.description}
							onChange={(e) => handleChange("description", e.target.value)}
						/>
					</div>

					{/* 인원 / 카테고리 / 상태 */}
					<div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
						<div className="space-y-2">
							<Label>최대 인원</Label>
							<Input
								type="number"
								min={2}
								value={form.maxMembers}
								onChange={(e) => handleChange("maxMembers", e.target.value)}
							/>
						</div>

						<div className="space-y-2">
							<Label>카테고리</Label>
							<Select
								value={form.category}
								onValueChange={(v) => handleChange("category", v)}
							>
								<SelectTrigger>
									<SelectValue placeholder="카테고리 선택" />
								</SelectTrigger>
								<SelectContent>
									<SelectItem value="BACKEND">백엔드</SelectItem>
									<SelectItem value="FRONTEND">프론트엔드</SelectItem>
									<SelectItem value="ALGORITHM">알고리즘</SelectItem>
									<SelectItem value="CS">CS</SelectItem>
									<SelectItem value="PROJECT">프로젝트</SelectItem>
								</SelectContent>
							</Select>
						</div>

						<div className="space-y-2">
							<Label>모집 상태</Label>
							<Select
								value={form.status}
								onValueChange={(v) => handleChange("status", v)}
							>
								<SelectTrigger>
									<SelectValue placeholder="상태 선택" />
								</SelectTrigger>
								<SelectContent>
									{STATUS_OPTIONS.map((opt) => (
										<SelectItem key={opt.value} value={opt.value}>
											{opt.label}
										</SelectItem>
									))}
								</SelectContent>
							</Select>
						</div>
					</div>

					{/* 태그 */}
					<div className="space-y-2">
						<Label>태그 검색</Label>
						<div className="relative">
							<Input
								placeholder="태그를 입력하세요"
								value={tagKeyword}
								onChange={(e) => setTagKeyword(e.target.value)}
								onKeyDown={handleKeyDown}
							/>
							{filteredTagResults.map((tag, index) => (
								<div
									key={tag}
									className={`cursor-pointer px-3 py-2 text-sm border border-t-0 hover:bg-gray-100
										${index === filteredTagResults.length - 1 ? "rounded-b-md" : ""}
										${index === selectedIndex ? "bg-gray-100" : ""}`}
									onMouseEnter={() => setSelectedIndex(index)}
									onClick={() => addTag(tag)}
								>
									{tag}
								</div>
							))}
						</div>
						<div className="flex flex-wrap gap-2">
							{form.tags.map((tag) => (
								<Badge key={tag} className="cursor-pointer" onClick={() => removeTag(tag)}>
									{tag} ✕
								</Badge>
							))}
						</div>
					</div>

					{/* 날짜 */}
					<div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
						<div className="space-y-2">
							<Label>스터디 시작일</Label>
							<Input
								type="date"
								value={form.startDate}
								onChange={(e) => handleChange("startDate", e.target.value)}
							/>
						</div>
						<div className="space-y-2">
							<Label>스터디 종료일 (선택)</Label>
							<Input
								type="date"
								min={form.startDate || today}
								value={form.endDate}
								onChange={(e) => handleChange("endDate", e.target.value)}
							/>
						</div>
					</div>
					<div className="space-y-2">
						<Label>모집 마감일</Label>
						<Input
							type="date"
							value={form.recruitEndDate}
							onChange={(e) => handleChange("recruitEndDate", e.target.value)}
						/>
					</div>

					{error && <p className="text-sm text-red-500">{error}</p>}

					{/* 버튼 */}
					<div className="flex justify-end gap-2 pt-6">
						<Button
							type="button"
							variant="outline"
							className="cursor-pointer"
							onClick={() => navigate(`/study/${id}`)}
						>
							취소
						</Button>
						<Button
							type="submit"
							className="cursor-pointer"
							disabled={isSubmitDisabled}
						>
							수정 완료
						</Button>
					</div>
				</form>
			</div>
		</div>
	);
};

export default StudyUpdatePage;
