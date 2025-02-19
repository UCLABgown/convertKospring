'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import Link from 'next/link'

export default function old_ClientPage({
  responseBody,
}: {
  responseBody: components['schemas']['ApiResponseListProductDto']
}) {
  return (
    <>
      <div className="flex flex-col">
        <h2 className="text-5xl font-extrabold mt-20 mb-10 text-center">
          Products
        </h2>
        <ul className="grid grid-cols-4 gap-6">
          {responseBody.content?.map((item) => (
            <li
              key={item.id}
              className="border-[1px] my-3 flex flex-col bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)]"
            >
              <div
                style={{
                  backgroundImage: `url(${item.imageUrl})`,
                  backgroundPosition: 'center',
                  backgroundSize: 'cover',
                  paddingBottom: '100%',
                }}
                className="border-[#eee] border-b"
              ></div>
              <div className="p-3 flex-grow grid grid-rows-[auto_1fr_auto_auto]">
                <div className="text-sm text-[#59473F] font-bold text-white py-1 px-2 rounded-md">
                  {item.category}
                </div>
                <div className="my-2 justify-self-start">
                  {' '}
                  {item.productName}
                </div>
                <div className="w-full justify-self-end text-3xl font-extrabold text-[#59473F]">
                  {Number(item.price).toLocaleString()}
                </div>
                <div className="mt-2 w-full justify-self-end relative">
                  <input
                    type="number"
                    className="w-[60px] border-[1px] border-[#ccc] rounded-[5px] p-2 h-[40px]"
                    defaultValue={1}
                  />
                  <button
                    type="button"
                    className="absolute right-0 -bottom-0 py-2 px-3 bg-[#59473F] text-white rounded-[8px] h-[40px]"
                  >
                    CART
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  )
}
