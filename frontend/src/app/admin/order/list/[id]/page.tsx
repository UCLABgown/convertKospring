import client from '@/lib/backend/client';
import ClientPage from './ClientPage';

export default async function Page({ params }: { params: { id: string } }) {
  const { id } = await params;

  try {
    const apiResponse = await client.GET(`/order/${id}`);

    if (apiResponse.response.error) {
      return <p>{apiResponse.response.error}</p>;
    }

    return <ClientPage order={apiResponse.data.content} />;
  } catch (error: any) {
    return <p>{error.message || '알 수 없는 오류가 발생했습니다.'}</p>;
  }
}