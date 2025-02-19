'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import React, { useState } from 'react'
import Link from 'next/link'

type ApiResponseListOrderResponseDTO = components['schemas']['ApiResponseListOrderResponseDTO']

export default function ClientPage({
  orders,
}: {
  orders: ApiResponseListOrderResponseDTO
}) {
  // 주문 데이터가 없을 경우 빈 배열로 초기화
  const orderList = Array.isArray(orders) ? orders : []

  // 검색어, 검색 기준, 현재 페이지 상태 관리
  const [searchTerm, setSearchTerm] = useState('')
  const [searchCriterion, setSearchCriterion] = useState<'id' | 'email'>('id')
  const [currentPage, setCurrentPage] = useState(1)
  const ordersPerPage = 10

z
  // 주문 목록 필터링: 검색 기준에 맞게 주문 필터링
  const filteredOrders = orderList.filter(
    (order) =>
      searchCriterion === 'id'
        ? order.id.toString().includes(searchTerm) // 주문 ID로 검색
        : order.email.includes(searchTerm), // 주문자 이메일로 검색
  )

  // 페이지에 표시할 주문 목록 계산
  const indexOfLastOrder = currentPage * ordersPerPage
  const indexOfFirstOrder = indexOfLastOrder - ordersPerPage
  const currentOrders = filteredOrders.slice(
    indexOfFirstOrder,
    indexOfLastOrder,
  )

  // 페이지 변경 함수
  const paginate = (pageNumber: number) => setCurrentPage(pageNumber)

  // 전체 페이지 수 계산
  const totalPages = Math.ceil(filteredOrders.length / ordersPerPage)

  // 수정 버튼 클릭 시 실행될 함수
  const handleEdit = (orderId: string) => {
    alert(`주문 ID ${orderId} 수정 클릭`)
    // 수정 페이지로 리디렉션하거나 수정 모달을 여는 로직을 추가할 수 있습니다.
  }

  return (
    <div>
      <h2 className="text-5xl font-extrabold mt-20 mb-12 text-center">
        주문 목록
      </h2>

      {currentOrders.length === 0 ? (
        <p style={{ textAlign: 'center', fontSize: '18px', color: '#555' }}>
          주문 내역이 없습니다.
        </p>
      ) : (
        <table className="table-fixed w-full bg-white mt-3 shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
          <colgroup>
            <col className="w-[120px]" />
            <col className="w-[300px]" />
            <col className="w-[150px]" />
            <col className="w-[150px]" />
            <col width="" />
            <col className="w-[180px]" />
            <col className="w-[180px]" />
            <col className="w-[150px]" />
          </colgroup>
          <thead className="text-center bg-[#59473F] text-white">
            <tr>
              <th className="py-3">주문 ID</th>
              <th>주문자 이메일</th>
              <th>주문 일자</th>
              <th>갱신 일자</th>
              <th>주문 내역</th>
              <th>주문 금액</th>
              <th>배송 상태</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {currentOrders.map((order) => (
              <tr
                key={order.id}
                className="border-b border-[#eee] bg-white text-center"
              >
                <td className="px-3">{order.id}</td>
                <td className="px-3">{order.email}</td>
                <td className="p-3 px-5">
                  <CustomTime input={order.createdAt.toLocaleString()} />
                </td>
                <td className="px-3 px-5">
                  <CustomTime input={order.modifiedAt.toLocaleString()} />
                </td>
                <td className="px-3">
                  {order.orderItems[0].productName.length > 8
                    ? order.orderItems[0].productName.substring(0, 8) + '...'
                    : order.orderItems[0].productName}
                  외 {order.orderItems.length - 1}건
                </td>
                <td className="px-3">{order.totalPrice.toLocaleString()}원</td>
                <td className="px-3">
                  {{
                    UNKNOWN: '알 수 없음',
                    CANCELLED: '취소',
                    PAYMENT_COMPLETED: '결제 완료',
                    PREPARING: '배송 준비',
                    SHIPPING: '배송 중',
                    COMPLETED: '배송 완료',
                  }[order.status] || '알 수 없음'}
                </td>
                <td className="px-3">
                  <Link
                    href={`/admin/order/list/${order.id}`}
                    className="py-2 px-3 bg-[#59473F] text-white rounded-[8px]"
                  >
                    수정
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <div className="flex mt-5 justify-center">
        <select
          value={searchCriterion}
          onChange={(e) => setSearchCriterion(e.target.value as 'id' | 'email')}
          className="py-3 px-5 mr-3 block w-[150px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
        >
          <option value="id">ID</option>
          <option value="email">이메일</option>
        </select>
        <input
          type="text"
          placeholder="검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="py-3 px-5 mr-3 block w-[270px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
        />
      </div>
      <div className="flex my-5 gap-6 justify-center items-center">
        <button
          onClick={() => paginate(currentPage - 1)}
          disabled={currentPage === 1}
          className={`px-3 py-2 bg-[#59473F] text-white rounded-[8px] shadow-[0_0_10px_0_rgba(0,0,0,0.1)] ${
            currentPage === 1 ? 'cursor-not-allowed' : 'cursor-pointer'
          }`}
        >
          이전
        </button>
        <span className="font-normal text-lg">
          {currentPage} / {totalPages}
        </span>
        <button
          onClick={() => paginate(currentPage + 1)}
          disabled={currentPage === totalPages}
          className={`px-3 py-2 bg-[#59473F] text-white rounded-[8px] shadow-[0_0_10px_0_rgba(0,0,0,0.1)] ${
            currentPage === totalPages ? 'cursor-not-allowed' : 'cursor-pointer'
          }`}
        >
          다음
        </button>
      </div>
    </div>
  )
}
