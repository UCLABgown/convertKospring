import client from '@/lib/backend/client';
import ClientPage from './ClientPage';

export default async function Page({ params }: { params: { id: string } }) {
  const { id } = await params;

  try {
    const response = await client.GET(`/order/${id}`);

    if (response.error) {
      return <p>{response.error}</p>;
    }

    return <ClientPage order={response.data.content} />;
  } catch (error: any) {
    return <p>{error.message || '알 수 없는 오류가 발생했습니다.'}</p>;
  }
}