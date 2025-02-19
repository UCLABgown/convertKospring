'use client'

import { useState } from 'react'
import type { components } from '@/lib/backend/apiV1/schema'
import client from '@/lib/backend/client'

type OrderResponseDTO = components['schemas']['OrderResponseDTO']

export default function ClientPage({ order }: { order: OrderResponseDTO }) {
  const [isDeleting, setIsDeleting] = useState(false)
  const [isUpdating, setIsUpdating] = useState(false)
  const [selectedStatus, setSelectedStatus] = useState(order.status)

  if (!order) {
    return <p>주문 데이터를 불러올 수 없습니다.</p>
  }

  const CustomTime = ({ input }: { input: string }) => {
    const parts = input.split(',')
    const year = parseInt(parts[0].trim() + parts[1].trim())
    const month = parseInt(parts[2].trim()).toString().padStart(2, '0')
    const day = parseInt(parts[3].trim()).toString().padStart(2, '0')
    var hour =
      parseInt(parts[4].trim()) >= 12
        ? parseInt(parts[4].trim()) - 12
        : parseInt(parts[4].trim())
    var after = parts[4] >= 12 ? '오후' : '오전'
    const minute = parseInt(parts[5].trim())
    return `${year}. ${month}. ${day}.   ${after} ${hour}시 ${minute}분`
  }

  const statusOptions = {
    UNKNOWN: '알 수 없음',
    CANCELLED: '취소',
    PAYMENT_COMPLETED: '결제 완료',
    PREPARING: '배송 준비',
    SHIPPING: '배송 중',
    COMPLETED: '배송 완료',
  }

  const handleUpdateStatus = async () => {
    if (isUpdating) return

    if (
      confirm(
        `배송 상태를 "${statusOptions[selectedStatus]}"(으)로 변경하시겠습니까?`,
      )
    ) {
      setIsUpdating(true)

      try {
        const apiResponse = await client.PUT(
          `/order/${order.id}/status?status=${encodeURIComponent(
            selectedStatus,
          )}`,
        )
        const response = apiResponse.response
        if (response.ok) {
          alert('배송 상태가 성공적으로 변경되었습니다.')
        } else {
          alert(response.error || '배송 상태 변경에 실패했습니다.')
        }
        window.location.href = '/admin/order/list'
      } catch (err) {
        alert('오류가 발생했습니다. 다시 시도해주세요.')
      } finally {
        setIsUpdating(false)
      }
    }
  }

  const handleDeleteOrder = async () => {
    if (isDeleting) return // 중복 클릭 방지

    if (confirm('정말로 이 주문을 삭제하시겠습니까?')) {
      setIsDeleting(true)

      try {
        const apiResponse = await client.DELETE(`/order/${order.id}`)
        const response = apiResponse.response
        if (response.ok) {
          alert('주문을 성공적으로 삭제했습니다.')
        } else {
          alert(response.error || '주문 삭제에 실패했습니다.')
        }
        window.location.href = '/admin/order/list'
      } catch (err) {
        setError(err.message || '알 수 없는 오류가 발생했습니다.')
      } finally {
        setIsDeleting(false)
      }
    }
  }

  return (
    <div className="max-w-[1200px] mx-auto">
      <h2 className="text-5xl font-extrabold mt-20 mb-12 text-center">
        주문 상세 정보
      </h2>

      {/* 주문 상세 정보 섹션 */}
      <table className="table-fixed w-full bg-white mt-3 mb-16 shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
        <colgroup>
          <col width="200" />
          <col width="" />
          <col width="" />
          <col width="" />
          <col width="" />
        </colgroup>
        <thead className="text-center bg-[#59473F] text-white">
          <tr>
            <th className="py-3">주문 ID</th>
            <th>주문 일자</th>
            <th>갱신 일자</th>
            <th>주문 금액</th>
            <th>배송 상태</th>
          </tr>
        </thead>
        <tbody>
          <tr className="bg-white text-center">
            <td className="px-3">{order.id}</td>
            <td className="px-3">
              <CustomTime input={order.createdAt.toLocaleString()} />
            </td>
            <td className="px-3">
              <CustomTime input={order.modifiedAt.toLocaleString()} />
            </td>
            <td className="px-3">{order.totalPrice.toLocaleString()}원</td>
            <td className="p-3">
              <select
                value={selectedStatus}
                onChange={(e) => setSelectedStatus(e.target.value)}
                className="py-3 px-5 mx-auto block w-[150px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
              >
                {Object.entries(statusOptions).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </td>
          </tr>
        </tbody>
      </table>

      {/* 주문 상품 목록 제목 */}
      <h3 className="text-3xl mb-5 font-semibold">주문 내역</h3>

      {/* 주문 상품 목록 섹션 */}
      {order.orderItems && order.orderItems.length > 0 ? (
        <ul style={{ listStyleType: 'none', padding: '0' }}>
          {order.orderItems.map((item, index) => (
            <li
              key={index}
              className="border border-[#eee] rounded-[8px] bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)] p-5 mb-5"
            >
              <p className="text-2xl font-bold mb-2">{item.productName}</p>
              <p className="text-lg text-gray-600">
                {item.count}개{' '}
                <span className="inline-block w-[1px] h-[13px] bg-gray-300 mx-3"></span>{' '}
                {item.price.toLocaleString()}원
              </p>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 text-center">주문 상품이 없습니다.</p>
      )}

      {/* 버튼들 */}
      <div className="mt-16 text-center">
        <button
          onClick={handleUpdateStatus}
          disabled={isUpdating}
          className="w-[150px] h-[50px] bg-[#59473F] text-white rounded-[8px] h-[40px]"
        >
          {isUpdating ? '변경 중...' : '배송 상태 변경'}
        </button>
        <button
          onClick={handleDeleteOrder}
          disabled={isDeleting}
          className="w-[150px] h-[50px] bg-red-800 text-white rounded-[8px] h-[40px] mx-5"
        >
          {isDeleting ? '삭제 중...' : '삭제'}
        </button>
        <button
          onClick={() => window.history.back()}
          className="w-[150px] h-[50px] bg-gray-400 text-white rounded-[8px] h-[40px] mr-5"
        >
          돌아가기
        </button>
      </div>
    </div>
  )
}
