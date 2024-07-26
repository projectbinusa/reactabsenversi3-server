import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faPrint, faXmark } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import Swal from "sweetalert2";
import { Pagination } from "flowbite-react";
import { API_DUMMY } from "../../../utils/api";

function Cuti() {
   const [userData, setUserData] = useState([]);
  const adminId = localStorage.getItem("adminId");

   const [cuti, setCuti] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [limit, setLimit] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const getAllCuti = async () => {
    const token = localStorage.getItem("token");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/cuti/admin/${adminId}`,
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );

      setCuti(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const DownloadPdfCuti = async (id) => {
    const token = localStorage.getItem("token");

    Swal.fire({
      title: "Konfirmasi",
      text: "Apakah Anda ingin mengunduh file PDF?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Ya, Unduh!",
      cancelButtonText: "Batal",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const response = await axios({
            url: `${API_DUMMY}/api/cuti/download-pdf/${id}`,
            method: "GET",
            responseType: "blob",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "Surat Cuti.pdf");
          document.body.appendChild(link);
          link.click();

          Swal.fire("Berhasil", "Berhasil Mengunduh Pdf", "success");
        } catch (error) {
          console.log(error);
          Swal.fire("Gagal", "Gagal Mengunduh Pdf", "error");
        }
      } else {
        Swal.fire("Dibatalkan", "Pengunduhan dibatalkan", "info");
      }
    });
  };

  const konfirmasiSetujuCuti = async (id) => {
    Swal.fire({
      title: "Konfirmasi",
      text: "Apakah yakin ingin menyetujui izin cuti ini?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#31363F",
      cancelButtonColor: "#d33",
      confirmButtonText: "Ya, Setuju!",
      cancelButtonText: "Batal",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axios.put(`${API_DUMMY}/api/cuti/terima-cuti/` + id, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
          setTimeout(() => {
            window.location.reload();
          }, 1500);
        } catch (error) {
          console.error(error);
          Swal.fire({
            icon: "error",
            title: "Gagal Menghapus Data",
          });
        }
      }
    });
  };
  const BatalkanCuti = async (id) => {
    Swal.fire({
      title: "Konfirmasi",
      text: "Apakah yakin ingin membatalkan izin cuti ini?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "rgb(185 28 28)",
      confirmButtonText: "Ya, Batalkan!",
      cancelButtonText: "Batal",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axios.put(`${API_DUMMY}/api/cuti/tolak-cuti/` + id, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
          setTimeout(() => {
            window.location.reload();
          }, 1500);
        } catch (error) {
          console.error(error);
          Swal.fire({
            icon: "error",
            title: "Gagal Menghapus Data",
          });
        }
      }
    });
  };
  useEffect(() => {
    getAllCuti();
  }, []);

  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  useEffect(() => {
    const filteredData = cuti.filter(
      (cuti) =>
        (cuti.user?.username.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (cuti.keperluan?.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (cuti.status?.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cuti.awalCuti)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cuti.akhirCuti)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cuti.masukKerja)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false)
    );
    setTotalPages(Math.ceil(filteredData.length / limit));
  }, [searchTerm, limit, cuti]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleLimitChange = (event) => {
    setLimit(parseInt(event.target.value));
    setCurrentPage(1); // Reset to the first page when limit changes
  };

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const filteredCuti = cuti.filter(
    (cuti) =>
      (cuti.user?.username.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (cuti.keperluan?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (cuti.status?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cuti.awalCuti)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cuti.akhirCuti)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cuti.masukKerja)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false)
  );

  const paginatedCuti = filteredCuti.slice(
    (currentPage - 1) * limit,
    currentPage * limit
  );

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-4 ml-0 md:ml-64 mt-5">
          <div className="p-5 mt-10 overflow-x-auto">
            {/* <!-- Card --> */}
            <div className="w-full text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex justify-between">
                <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                  Permohonan Cuti
                </h6>
                <div className="flex items-center gap-2 mt-2">
                  <div className="relative w-64">
                    <input
                      type="search"
                      id="search-dropdown"
                      value={searchTerm}
                      onChange={handleSearch}
                      className="block p-2.5 w-full z-20 text-sm rounded-l-md text-gray-900 bg-gray-50 border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500"
                      placeholder="Search name..."
                      required
                    />
                  </div>
                  <select
                    value={limit}
                    onChange={handleLimitChange}
                    className="flex-shrink-0 z-10 inline-flex rounded-r-md items-center py-2.5 px-4 text-sm font-medium text-gray-900 bg-gray-100 border border-gray-300 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600"
                  >
                    <option value="5">05</option>
                    <option value="10">10</option>
                    <option value="20">20</option>
                    <option value="50">50</option>
                  </select>
                </div>
              </div>
              <hr className="mt-3" />

              {/* <!-- Tabel --> */}
              <div className=" overflow-x-auto mt-5">
                <table
                  id="dataCuti"
                  className="w-full text-center text-sm  text-gray-500 dark:text-gray-400"
                >
                  {/* <!-- Tabel Head --> */}
                  <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                    <tr>
                      <th scope="col" className="px-6 py-3">
                        No
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Nama
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Cuti Dari
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Sampai
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Masuk Kerja
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Keperluan
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Status
                      </th>
                      <th scope="col" className="px-6 py-3 text-center">
                        Aksi
                      </th>
                    </tr>
                  </thead>

                  {/* <!-- Tabel Body --> */}
                  <tbody className="text-left">
                    {paginatedCuti.map((cuti, index) => (
                      <tr
                        className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                        key={index}
                      >
                        <th
                          scope="row"
                          className="px-2 py-4 font-medium text-gray-900 dark:text-white"
                        >
                          {(currentPage - 1) * limit + index + 1}
                        </th>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {cuti.user.username}
                        </td>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {formatDate(cuti.awalCuti)}
                        </td>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {formatDate(cuti.akhirCuti)}
                        </td>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {formatDate(cuti.masukKerja)}
                        </td>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {cuti.keperluan}
                        </td>
                        <td className="px-2 py-2 text-gray-700 text-center capitalize">
                          {cuti.status}
                        </td>
                        <td className="py-3">
                          <div className="flex items-center -space-x-4 ml-12">
                            <button
                              className="z-20 block rounded-full border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50"
                              onClick={() => konfirmasiSetujuCuti(cuti.id)}
                            >
                              <span className="relative inline-block">
                                <FontAwesomeIcon
                                  icon={faCheck}
                                  className="h-4 w-4"
                                />
                              </span>
                            </button>

                            <button
                              className="z-30 block rounded-full border-2 border-white bg-red-100 p-4 text-red-700 active:bg-red-50"
                              onClick={() => BatalkanCuti(cuti.id)}
                            >
                              <span className="relative inline-block">
                                <FontAwesomeIcon
                                  icon={faXmark}
                                  className="h-4 w-4"
                                />
                              </span>
                            </button>

                            <a onClick={() => DownloadPdfCuti(cuti.id)}>
                              <button className="z-30 block rounded-full border-2 border-white  bg-yellow-100 p-4 text-yellow-700 active:bg-red-50">
                                <span className="relative inline-block">
                                  <FontAwesomeIcon
                                    icon={faPrint}
                                    className="h-4 w-4"
                                  />
                                </span>
                              </button>
                            </a>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <Pagination
                className="mt-5"
                layout="table"
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={onPageChange}
                showIcons
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Cuti;
